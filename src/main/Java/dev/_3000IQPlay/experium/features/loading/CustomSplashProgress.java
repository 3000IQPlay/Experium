package dev._3000IQPlay.experium.features.loading;

import dev._3000IQPlay.experium.features.loading.Shader2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.asm.FMLSanityChecker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class CustomSplashProgress {
    private static Drawable d;
    private static volatile boolean pause;
    private static volatile boolean done;
    private static Thread thread;
    private static volatile Throwable threadError;
    private static int angle;
    private static final Lock lock;
    private static SplashFontRenderer fontRenderer;
    private static final IResourcePack mcPack;
    private static final IResourcePack fmlPack;
    private static IResourcePack miscPack;
    private static Texture fontTexture;
    private static Texture logoTexture;
    private static Texture forgeTexture;
    private static Texture mrleanTexture;
    private static Properties config;
    private static boolean enabled;
    private static boolean rotate;
    private static int logoOffset;
    private static int backgroundColor;
    private static int fontColor;
    private static int barBorderColor;
    private static int barColor;
    private static int barBackgroundColor;
    private static boolean showMemory;
    private static int memoryGoodColor;
    private static int memoryWarnColor;
    private static int memoryLowColor;
    private static float memoryColorPercent;
    private static long memoryColorChangeTime;
    static boolean isDisplayVSyncForced;
    private static final int TIMING_FRAME_COUNT = 200;
    private static final int TIMING_FRAME_THRESHOLD = 1000000000;
    static final Semaphore mutex;
    private static int max_texture_size;
    private static final IntBuffer buf;

    private static String getString(String name, String def) {
        String value = config.getProperty(name, def);
        config.setProperty(name, value);
        return value;
    }

    private static boolean getBool(String name, boolean def) {
        return Boolean.parseBoolean(CustomSplashProgress.getString(name, Boolean.toString(def)));
    }

    private static int getInt(String name, int def) {
        return Integer.decode(CustomSplashProgress.getString(name, Integer.toString(def)));
    }

    private static int getHex(String name, int def) {
        return Integer.decode(CustomSplashProgress.getString(name, "0x" + Integer.toString(def, 16).toUpperCase()));
    }

    public static void start() throws IOException {
        File configFile = new File(Minecraft.getMinecraft().gameDir, "config/splash.properties");
        File parent = configFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        config = new Properties();
        try (InputStreamReader r = new InputStreamReader((InputStream)new FileInputStream(configFile), StandardCharsets.UTF_8);){
            config.load(r);
        }
        catch (IOException e) {
            FMLLog.log.info("Could not load splash.properties, will create a default one");
        }
        boolean defaultEnabled = true;
        enabled = CustomSplashProgress.getBool("enabled", defaultEnabled) && (!FMLClientHandler.instance().hasOptifine() || Launch.blackboard.containsKey("optifine.ForgeSplashCompatible"));
        rotate = CustomSplashProgress.getBool("rotate", false);
        showMemory = CustomSplashProgress.getBool("showMemory", true);
        logoOffset = CustomSplashProgress.getInt("logoOffset", 0);
        backgroundColor = CustomSplashProgress.getHex("background", 0xFFFFFF);
        fontColor = CustomSplashProgress.getHex("font", 0);
        barBorderColor = CustomSplashProgress.getHex("barBorder", 0xC0C0C0);
        barColor = CustomSplashProgress.getHex("bar", 13319477);
        barBackgroundColor = CustomSplashProgress.getHex("barBackground", 0xFFFFFF);
        memoryGoodColor = CustomSplashProgress.getHex("memoryGood", 7916340);
        memoryWarnColor = CustomSplashProgress.getHex("memoryWarn", 15132746);
        memoryLowColor = CustomSplashProgress.getHex("memoryLow", 14954287);
        final ResourceLocation fontLoc = new ResourceLocation(CustomSplashProgress.getString("fontTexture", "textures/font/ascii.png"));
        final ResourceLocation logoLoc = new ResourceLocation("textures/gui/title/mojang.png");
        final ResourceLocation forgeLoc = new ResourceLocation(CustomSplashProgress.getString("forgeTexture", "fml:textures/gui/forge.png"));
        final ResourceLocation forgeFallbackLoc = new ResourceLocation("fml:textures/gui/forge.png");
        final ResourceLocation exspermium = new ResourceLocation("textures/experium.png");
        File miscPackFile = new File(Minecraft.getMinecraft().gameDir, CustomSplashProgress.getString("resourcePackPath", "resources"));
        try (OutputStreamWriter w = new OutputStreamWriter((OutputStream)new FileOutputStream(configFile), StandardCharsets.UTF_8);){
            config.store(w, "Splash screen properties");
        }
        catch (IOException e) {
            FMLLog.log.error("Could not save the splash.properties file", (Throwable)e);
        }
        miscPack = CustomSplashProgress.createResourcePack(miscPackFile);
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable(){

            public String call() throws Exception {
                return "' Vendor: '" + GL11.glGetString((int)7936) + "' Version: '" + GL11.glGetString((int)7938) + "' Renderer: '" + GL11.glGetString((int)7937) + "'";
            }

            public String getLabel() {
                return "GL info";
            }
        });
        CrashReport report = CrashReport.makeCrashReport((Throwable)new Throwable(), (String)"Loading screen debug info");
        StringBuilder systemDetailsBuilder = new StringBuilder();
        report.getCategory().appendToStringBuilder(systemDetailsBuilder);
        FMLLog.log.info(systemDetailsBuilder.toString());
        try {
            d = new SharedDrawable(Display.getDrawable());
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        }
        catch (LWJGLException e) {
            FMLLog.log.error("Error starting SplashProgress:", (Throwable)e);
            CustomSplashProgress.disableSplash((Exception)((Object)e));
        }
        CustomSplashProgress.getMaxTextureSize();
        final Shader2D glslShader = new Shader2D("/assets/experium/shader/loading/vertex.glsl", "/assets/experium/shader/loading/og.glsl");
        final long startTime = System.currentTimeMillis();
        thread = new Thread(new Runnable(){
            private final int barWidth = 400;
            private final int barHeight = 20;
            private final int textHeight2 = 20;
            private final int barOffset = 55;
            private long updateTiming;
            private long framecount;

            @Override
            public void run() {
                this.setGL();
                fontTexture = new Texture(fontLoc, null);
                logoTexture = new Texture(logoLoc, null, false);
                forgeTexture = new Texture(forgeLoc, forgeFallbackLoc);
                mrleanTexture = new Texture(exspermium, null);
                GL11.glEnable((int)3553);
                fontRenderer = new SplashFontRenderer();
                GL11.glDisable((int)3553);
                Color barColor = new Color(2068861008, true);
                while (!done) {
                    ++this.framecount;
                    ProgressManager.ProgressBar first = null;
                    ProgressManager.ProgressBar penult = null;
                    ProgressManager.ProgressBar last = null;
                    Iterator i = ProgressManager.barIterator();
                    while (i.hasNext()) {
                        if (first == null) {
                            first = (ProgressManager.ProgressBar)i.next();
                            continue;
                        }
                        penult = last;
                        last = (ProgressManager.ProgressBar)i.next();
                    }
                    GL11.glPushMatrix();
                    GL11.glClear((int)16384);
                    int w = Display.getWidth();
                    int h = Display.getHeight();
                    GL11.glViewport((int)0, (int)0, (int)w, (int)h);
                    GL11.glMatrixMode((int)5889);
                    GL11.glLoadIdentity();
                    GL11.glOrtho((double)(320 - w / 2), (double)(320 + w / 2), (double)(240 + h / 2), (double)(240 - h / 2), (double)-1.0, (double)1.0);
                    GL11.glMatrixMode((int)5888);
                    GL11.glLoadIdentity();
                    int left = 320 - w / 2;
                    int right = 320 + w / 2;
                    int bottom = 240 + h / 2;
                    int top = 240 - h / 2;
                    GL11.glPushMatrix();
                    this.setColor(0xFFFFFF);
                    glslShader.useShader(Math.abs(320 + w / 2 - (320 - w / 2)), Math.abs(240 + h / 2 - (240 - h / 2)), (float)(System.currentTimeMillis() - startTime) / 69.0f);
                    GL11.glBegin((int)7);
                    GL11.glVertex2f((float)left, (float)bottom);
                    GL11.glVertex2f((float)right, (float)bottom);
                    GL11.glVertex2f((float)right, (float)top);
                    GL11.glVertex2f((float)left, (float)top);
                    GL11.glEnd();
                    GL20.glUseProgram((int)0);
                    GL11.glPopMatrix();
                    this.setColor(0xFFFFFF);
                    float midX = (float)right - (float)(right - left) / 2.0f;
                    float midY = (float)bottom - (float)(bottom - top) / 2.0f;
                    float mrLeanThiccness = (float)h * 0.7f;
                    GL11.glColor4f((float)((float)barColor.getRed() / 255.0f), (float)((float)barColor.getGreen() / 255.0f), (float)((float)barColor.getBlue() / 255.0f), (float)((float)barColor.getAlpha() / 255.0f));
                    int barTop = bottom - 75;
                    GL11.glBegin((int)7);
                    GL11.glVertex2f((float)left, (float)bottom);
                    GL11.glVertex2f((float)right, (float)bottom);
                    GL11.glVertex2f((float)right, (float)barTop);
                    GL11.glVertex2f((float)left, (float)barTop);
                    GL11.glEnd();
                    GL11.glPushMatrix();
                    int scale = 2;
                    int fontY = bottom - 37 - fontRenderer.FONT_HEIGHT;
                    int fontX = left + 10;
                    String progress = "Error";
                    if (first != null) {
                        progress = first.getTitle() + " - " + first.getMessage();
                    }
                    this.setColor(0xFFFFFF);
                    GL11.glScalef((float)scale, (float)scale, (float)1.0f);
                    GL11.glEnable((int)3553);
                    fontRenderer.drawString("Violet v2.0 | " + progress, fontX / scale, fontY / scale, 0xFFFFFF);
                    GL11.glDisable((int)3553);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    mutex.acquireUninterruptibly();
                    long updateStart = System.nanoTime();
                    Display.update();
                    long dur = System.nanoTime() - updateStart;
                    if (this.framecount < 200L) {
                        this.updateTiming += dur;
                    }
                    mutex.release();
                    if (pause) {
                        this.clearGL();
                        this.setGL();
                    }
                    if (this.framecount >= 200L && this.updateTiming > 1000000000L) {
                        if (!isDisplayVSyncForced) {
                            isDisplayVSyncForced = true;
                            FMLLog.log.info("Using alternative sync timing : {} frames of Display.update took {} nanos", (Object)200, (Object)this.updateTiming);
                        }
                        try {
                            Thread.sleep(16L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    if (this.framecount == 200L) {
                        FMLLog.log.info("Using sync timing. {} frames of Display.update took {} nanos", (Object)200, (Object)this.updateTiming);
                    }
                    Display.sync((int)100);
                }
                this.clearGL();
            }

            private void setColor(int color) {
                GL11.glColor3ub((byte)((byte)(color >> 16 & 0xFF)), (byte)((byte)(color >> 8 & 0xFF)), (byte)((byte)(color & 0xFF)));
            }

            private void drawBox(int w, int h) {
                GL11.glBegin((int)7);
                GL11.glVertex2f((float)0.0f, (float)0.0f);
                GL11.glVertex2f((float)0.0f, (float)h);
                GL11.glVertex2f((float)w, (float)h);
                GL11.glVertex2f((float)w, (float)0.0f);
                GL11.glEnd();
            }

            private void drawBar(ProgressManager.ProgressBar b) {
                GL11.glPushMatrix();
                this.setColor(fontColor);
                GL11.glScalef((float)2.0f, (float)2.0f, (float)1.0f);
                GL11.glEnable((int)3553);
                fontRenderer.drawString(b.getTitle() + " - " + b.getMessage(), 0, 0, 0);
                GL11.glDisable((int)3553);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.0f, (float)20.0f, (float)0.0f);
                this.setColor(barBorderColor);
                this.drawBox(400, 20);
                this.setColor(barBackgroundColor);
                GL11.glTranslatef((float)1.0f, (float)1.0f, (float)0.0f);
                this.drawBox(398, 18);
                this.setColor(barColor);
                this.drawBox(398 * (b.getStep() + 1) / (b.getSteps() + 1), 18);
                String progress = "" + b.getStep() + "/" + b.getSteps();
                GL11.glTranslatef((float)(199.0f - (float)fontRenderer.getStringWidth(progress)), (float)2.0f, (float)0.0f);
                this.setColor(fontColor);
                GL11.glScalef((float)2.0f, (float)2.0f, (float)1.0f);
                GL11.glEnable((int)3553);
                fontRenderer.drawString(progress, 0, 0, 0);
                GL11.glPopMatrix();
            }

            private void drawMemoryBar() {
                int maxMemory = CustomSplashProgress.bytesToMb(Runtime.getRuntime().maxMemory());
                int totalMemory = CustomSplashProgress.bytesToMb(Runtime.getRuntime().totalMemory());
                int freeMemory = CustomSplashProgress.bytesToMb(Runtime.getRuntime().freeMemory());
                int usedMemory = totalMemory - freeMemory;
                float usedMemoryPercent = (float)usedMemory / (float)maxMemory;
                GL11.glPushMatrix();
                this.setColor(fontColor);
                GL11.glScalef((float)2.0f, (float)2.0f, (float)1.0f);
                GL11.glEnable((int)3553);
                fontRenderer.drawString("Jews Used / Total", 0, 0, 0);
                GL11.glDisable((int)3553);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)0.0f, (float)20.0f, (float)0.0f);
                this.setColor(barBorderColor);
                this.drawBox(400, 20);
                this.setColor(barBackgroundColor);
                GL11.glTranslatef((float)1.0f, (float)1.0f, (float)0.0f);
                this.drawBox(398, 18);
                long time = System.currentTimeMillis();
                if (usedMemoryPercent > memoryColorPercent || time - memoryColorChangeTime > 1000L) {
                    memoryColorChangeTime = time;
                    memoryColorPercent = usedMemoryPercent;
                }
                int memoryBarColor = memoryColorPercent < 0.75f ? memoryGoodColor : (memoryColorPercent < 0.85f ? memoryWarnColor : memoryLowColor);
                this.setColor(memoryLowColor);
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(398 * totalMemory / maxMemory - 2), (float)0.0f, (float)0.0f);
                this.drawBox(2, 18);
                GL11.glPopMatrix();
                this.setColor(memoryBarColor);
                this.drawBox(398 * usedMemory / maxMemory, 18);
                String progress = this.getMemoryString(usedMemory) + " / " + this.getMemoryString(maxMemory);
                GL11.glTranslatef((float)(199.0f - (float)fontRenderer.getStringWidth(progress)), (float)2.0f, (float)0.0f);
                this.setColor(fontColor);
                GL11.glScalef((float)2.0f, (float)2.0f, (float)1.0f);
                GL11.glEnable((int)3553);
                fontRenderer.drawString(progress, 0, 0, 0);
                GL11.glPopMatrix();
            }

            private String getMemoryString(int memory) {
                return StringUtils.leftPad((String)Integer.toString(memory), (int)4, (char)' ') + " MB";
            }

            private void setGL() {
                System.out.println("Setting Violet Loading Screen!");
                lock.lock();
                try {
                    Display.getDrawable().makeCurrent();
                }
                catch (LWJGLException e) {
                    FMLLog.log.error("Error setting GL context:", (Throwable)e);
                    throw new RuntimeException(e);
                }
                backgroundColor = Color.cyan.getRGB();
                GL11.glClearColor((float)((float)(backgroundColor >> 16 & 0xFF) / 255.0f), (float)((float)(backgroundColor >> 8 & 0xFF) / 255.0f), (float)((float)(backgroundColor & 0xFF) / 255.0f), (float)1.0f);
                GL11.glDisable((int)2896);
                GL11.glDisable((int)2929);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
            }

            private void clearGL() {
                Minecraft mc = Minecraft.getMinecraft();
                mc.displayWidth = Display.getWidth();
                mc.displayHeight = Display.getHeight();
                mc.resize(mc.displayWidth, mc.displayHeight);
                GL11.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glEnable((int)2929);
                GL11.glDepthFunc((int)515);
                GL11.glEnable((int)3008);
                GL11.glAlphaFunc((int)516, (float)0.1f);
                try {
                    Display.getDrawable().releaseContext();
                }
                catch (LWJGLException e) {
                    FMLLog.log.error("Error releasing GL context:", (Throwable)e);
                    throw new RuntimeException(e);
                }
                finally {
                    lock.unlock();
                }
            }
        });
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                FMLLog.log.error("Splash thread Exception", e);
                threadError = e;
            }
        });
        thread.start();
        CustomSplashProgress.checkThreadState();
    }

    public static int getMaxTextureSize() {
        if (max_texture_size != -1) {
            return max_texture_size;
        }
        for (int i = 16384; i > 0; i >>= 1) {
            GlStateManager.glTexImage2D((int)32868, (int)0, (int)6408, (int)i, (int)i, (int)0, (int)6408, (int)5121, null);
            if (GlStateManager.glGetTexLevelParameteri((int)32868, (int)0, (int)4096) == 0) continue;
            max_texture_size = i;
            return i;
        }
        return -1;
    }

    private static void checkThreadState() {
        if (thread.getState() == Thread.State.TERMINATED || threadError != null) {
            throw new IllegalStateException("Splash thread", threadError);
        }
    }

    @Deprecated
    public static void pause() {
        CustomSplashProgress.checkThreadState();
        pause = true;
        lock.lock();
        try {
            d.releaseContext();
            Display.getDrawable().makeCurrent();
        }
        catch (LWJGLException e) {
            FMLLog.log.error("Error setting GL context:", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static void resume() {
        CustomSplashProgress.checkThreadState();
        pause = false;
        try {
            Display.getDrawable().releaseContext();
            d.makeCurrent();
        }
        catch (LWJGLException e) {
            FMLLog.log.error("Error releasing GL context:", (Throwable)e);
            throw new RuntimeException(e);
        }
        lock.unlock();
    }

    public static void finish() {
        try {
            CustomSplashProgress.checkThreadState();
            done = true;
            thread.join();
            GL11.glFlush();
            d.releaseContext();
            Display.getDrawable().makeCurrent();
            fontTexture.delete();
            logoTexture.delete();
            forgeTexture.delete();
        }
        catch (Exception e) {
            FMLLog.log.error("Error finishing SplashProgress:", (Throwable)e);
            CustomSplashProgress.disableSplash(e);
        }
    }

    private static boolean disableSplash(Exception e) {
        if (CustomSplashProgress.disableSplash()) {
            throw new EnhancedRuntimeException(e){

                protected void printStackTrace(EnhancedRuntimeException.WrappedPrintStream stream) {
                    stream.println("SplashProgress has detected a error loading Minecraft.");
                    stream.println("This can sometimes be caused by bad video drivers.");
                    stream.println("We have automatically disabled the new Splash Screen in config/splash.properties.");
                    stream.println("Try reloading minecraft before reporting any errors.");
                }
            };
        }
        throw new EnhancedRuntimeException(e){

            protected void printStackTrace(EnhancedRuntimeException.WrappedPrintStream stream) {
                stream.println("SplashProgress has detected a error loading Minecraft.");
                stream.println("This can sometimes be caused by bad video drivers.");
                stream.println("Please try disabling the new Splash Screen in config/splash.properties.");
                stream.println("After doing so, try reloading minecraft before reporting any errors.");
            }
        };
    }

    private static boolean disableSplash() {
        File configFile = new File(Minecraft.getMinecraft().gameDir, "config/splash.properties");
        File parent = configFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        enabled = false;
        config.setProperty("enabled", "false");
        try (OutputStreamWriter w = new OutputStreamWriter((OutputStream)new FileOutputStream(configFile), StandardCharsets.UTF_8);){
            config.store(w, "Splash screen properties");
        }
        catch (IOException e) {
            FMLLog.log.error("Could not save the splash.properties file", (Throwable)e);
            return false;
        }
        return true;
    }

    private static IResourcePack createResourcePack(File file) {
        if (file.isDirectory()) {
            return new FolderResourcePack(file);
        }
        return new FileResourcePack(file);
    }

    public static void drawVanillaScreen(TextureManager renderEngine) throws LWJGLException {
        if (!enabled) {
            Minecraft.getMinecraft().drawSplashScreen(renderEngine);
        }
    }

    public static void clearVanillaResources(TextureManager renderEngine, ResourceLocation mojangLogo) {
        if (!enabled) {
            renderEngine.deleteTexture(mojangLogo);
        }
    }

    public static void checkGLError(String where) {
        int err = GL11.glGetError();
        if (err != 0) {
            throw new IllegalStateException(where + ": " + GLU.gluErrorString((int)err));
        }
    }

    private static InputStream open(ResourceLocation loc, @Nullable ResourceLocation fallback, boolean allowResourcePack) throws IOException {
        if (!allowResourcePack) {
            return mcPack.getInputStream(loc);
        }
        if (miscPack.resourceExists(loc)) {
            return miscPack.getInputStream(loc);
        }
        if (fmlPack.resourceExists(loc)) {
            return fmlPack.getInputStream(loc);
        }
        if (!mcPack.resourceExists(loc) && fallback != null) {
            return CustomSplashProgress.open(fallback, null, true);
        }
        return mcPack.getInputStream(loc);
    }

    private static int bytesToMb(long bytes) {
        return (int)(bytes / 1024L / 1024L);
    }

    static {
        pause = false;
        done = false;
        angle = 0;
        lock = new ReentrantLock(true);
        mcPack = Minecraft.getMinecraft().defaultResourcePack;
        fmlPack = CustomSplashProgress.createResourcePack(FMLSanityChecker.fmlLocation);
        isDisplayVSyncForced = false;
        mutex = new Semaphore(1);
        max_texture_size = -1;
        buf = BufferUtils.createIntBuffer((int)0x400000);
    }

    private static class SplashFontRenderer
    extends FontRenderer {
        public SplashFontRenderer() {
            super(Minecraft.getMinecraft().gameSettings, fontTexture.getLocation(), null, false);
            super.onResourceManagerReload(null);
        }

        protected void bindTexture(@Nonnull ResourceLocation location) {
            if (location != this.locationFontTexture) {
                throw new IllegalArgumentException();
            }
            fontTexture.bind();
        }

        @Nonnull
        protected IResource getResource(@Nonnull ResourceLocation location) throws IOException {
            DefaultResourcePack pack = Minecraft.getMinecraft().defaultResourcePack;
            return new SimpleResource(pack.getPackName(), location, pack.getInputStream(location), null, null);
        }
    }

    private static class Texture {
        private final ResourceLocation location;
        private final int name;
        private final int width;
        private final int height;
        private final int frames;
        private final int size;

        public Texture(ResourceLocation location, @Nullable ResourceLocation fallback) {
            this(location, fallback, true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public Texture(ResourceLocation location, @Nullable ResourceLocation fallback, boolean allowRP) {
            InputStream s = null;
            try {
                this.location = location;
                s = CustomSplashProgress.open(location, fallback, allowRP);
                ImageInputStream stream = ImageIO.createImageInputStream(s);
                Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
                if (!readers.hasNext()) {
                    throw new IOException("No suitable reader found for image" + (Object)location);
                }
                ImageReader reader = readers.next();
                reader.setInput(stream);
                int frames = reader.getNumImages(true);
                BufferedImage[] images = new BufferedImage[frames];
                for (int i = 0; i < frames; ++i) {
                    images[i] = reader.read(i);
                }
                reader.dispose();
                this.width = images[0].getWidth();
                int height = images[0].getHeight();
                if (height > this.width && height % this.width == 0) {
                    frames = height / this.width;
                    BufferedImage original = images[0];
                    height = this.width;
                    images = new BufferedImage[frames];
                    for (int i = 0; i < frames; ++i) {
                        images[i] = original.getSubimage(0, i * height, this.width, height);
                    }
                }
                this.frames = frames;
                this.height = height;
                int size = 1;
                while (size / this.width * (size / height) < frames) {
                    size *= 2;
                }
                this.size = size;
                GL11.glEnable((int)3553);
                Class<CustomSplashProgress> i = CustomSplashProgress.class;
                synchronized (CustomSplashProgress.class) {
                    this.name = GL11.glGenTextures();
                    GL11.glBindTexture((int)3553, (int)this.name);
                    // ** MonitorExit[i] (shouldn't be in output)
                    GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
                    GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
                    GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)size, (int)size, (int)0, (int)32993, (int)33639, (IntBuffer)null);
                    CustomSplashProgress.checkGLError("Texture creation");
                    int i2 = 0;
                    while (i2 * (size / this.width) < frames) {
                        for (int j = 0; i2 * (size / this.width) + j < frames && j < size / this.width; ++j) {
                            buf.clear();
                            BufferedImage image = images[i2 * (size / this.width) + j];
                            for (int k = 0; k < height; ++k) {
                                for (int l = 0; l < this.width; ++l) {
                                    buf.put(image.getRGB(l, k));
                                }
                            }
                            buf.position(0).limit(this.width * height);
                            GL11.glTexSubImage2D((int)3553, (int)0, (int)(j * this.width), (int)(i2 * height), (int)this.width, (int)height, (int)32993, (int)33639, (IntBuffer)buf);
                            CustomSplashProgress.checkGLError("Texture uploading");
                        }
                        ++i2;
                    }
                    GL11.glBindTexture((int)3553, (int)0);
                    GL11.glDisable((int)3553);
                }
            }
            catch (IOException e) {
                try {
                    FMLLog.log.error("Error reading texture from file: {}", (Object)location, (Object)e);
                    throw new RuntimeException(e);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(s);
                    throw throwable;
                }
            }
            {
                IOUtils.closeQuietly((InputStream)s);
                return;
            }
        }

        public ResourceLocation getLocation() {
            return this.location;
        }

        public int getName() {
            return this.name;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getFrames() {
            return this.frames;
        }

        public int getSize() {
            return this.size;
        }

        public void bind() {
            GL11.glBindTexture((int)3553, (int)this.name);
        }

        public void delete() {
            GL11.glDeleteTextures((int)this.name);
        }

        public float getU(int frame, float u) {
            return (float)this.width * ((float)(frame % (this.size / this.width)) + u) / (float)this.size;
        }

        public float getV(int frame, float v) {
            return (float)this.height * ((float)(frame / (this.size / this.width)) + v) / (float)this.size;
        }

        public void texCoord(int frame, float u, float v) {
            GL11.glTexCoord2f((float)this.getU(frame, u), (float)this.getV(frame, v));
        }
    }
}