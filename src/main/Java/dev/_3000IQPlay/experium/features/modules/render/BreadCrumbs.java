package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.event.events.WorldEvent;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BreadCrumbs
        extends Module {
    private final Setting<Integer> colorRed = this.register(new Setting<Integer>("Red", 135, 0, 255));
    private final Setting<Integer> colorGreen = this.register(new Setting<Integer>("Green", 0, 0, 255));
    private final Setting<Integer> colorBlue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
	private final Setting<Float> width = this.register(new Setting<Float>("Width", Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(15.599429f) ^ 0x7EB99743)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(2.076195f) ^ 0x7F04E061)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.3190416f) ^ 0x7F08D65B))));
    private final Setting<Boolean> fade = this.register(new Setting<Boolean>("Fade", false));
    private final Setting<Integer> fadeTime = this.register(new Setting<Integer>("FadeTime", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(100), _hidden -> this.fade.getValue()));
    private final List<BreadCrumbPoint> points = new ArrayList<BreadCrumbPoint>();
    private final long startTime = System.currentTimeMillis();

    public BreadCrumbs() {
        super("BreadCrumbs", "Makes a line", Module.Category.RENDER, true, false, false);
    }

    private Color getColor() {
        return new Color(this.colorRed.getValue(), this.colorGreen.getValue(), this.colorBlue.getValue());
    }
	
    @Override
    public void onRender3D(Render3DEvent event) {
        int fTime = this.fadeTime.getValue() * 100;
        long fadeSec = System.currentTimeMillis() - (long)fTime;
        List<BreadCrumbPoint> list = this.points;
        synchronized (list) {
            GL11.glPushMatrix();
            GL11.glDisable((int)3553);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glEnable((int)2848);
            GL11.glEnable((int)3042);
            GL11.glDisable((int)2929);
            BreadCrumbs.mc.entityRenderer.disableLightmap();
            GL11.glLineWidth(width.getValue().floatValue());
            GL11.glBegin((int)3);
            double renderPosX = BreadCrumbs.mc.renderManager.viewerPosX;
            double renderPosY = BreadCrumbs.mc.renderManager.viewerPosY;
            double renderPosZ = BreadCrumbs.mc.renderManager.viewerPosZ;
            if (this.fade.getValue().booleanValue()) {
                this.points.removeIf(BreadCrumbPoint -> {
                    float pct = (float)(((BreadCrumbPoint)BreadCrumbPoint).time - fadeSec) / (float)fTime;
                    return pct < 0.0f || pct > 1.0f;
                });
            }
            this.points.forEach(BreadCrumbPoint -> {
                BreadCrumbs.glColor(((BreadCrumbPoint)BreadCrumbPoint).color, 1.0f);
                GL11.glVertex3d((double)(((BreadCrumbPoint)BreadCrumbPoint).x - renderPosX), (double)(((BreadCrumbPoint)BreadCrumbPoint).y - renderPosY), (double)(((BreadCrumbPoint)BreadCrumbPoint).z - renderPosZ));
            });
            GL11.glColor4d((double)1.0, (double)1.0, (double)1.0, (double)1.0);
            GL11.glEnd();
            GL11.glEnable((int)2929);
            GL11.glDisable((int)2848);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)3553);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void onUpdate() {
        List<BreadCrumbPoint> list = this.points;
        synchronized (list) {
            this.points.add(new BreadCrumbPoint(BreadCrumbs.mc.player.posX, BreadCrumbs.mc.player.getEntityBoundingBox().minY, BreadCrumbs.mc.player.posZ, System.currentTimeMillis(), this.getColor().getRGB()));
        }
    }
	
    @SubscribeEvent
    public void onWorldLoad(WorldEvent event) {
        List<BreadCrumbPoint> list = this.points;
        synchronized (list) {
            this.points.clear();
        }
    }

    @Override
    public void onDisable() {
        List<BreadCrumbPoint> list = this.points;
        synchronized (list) {
            this.points.clear();
        }
    }

    private static void glColor(int hex, float alpha) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static class BreadCrumbPoint {
        private double x;
        private double y;
        private double z;
        private long time;
        private int color;

        public double getX() {
            return this.x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return this.y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return this.z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public long getTime() {
            return this.time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getColor() {
            return this.color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public BreadCrumbPoint(double x, double y, double z, long time, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = time;
            this.color = color;
        }
    }
}