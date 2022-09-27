package dev._3000IQPlay.experium.util.shaders.impl.outline;

import dev._3000IQPlay.experium.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.HashMap;

public final class SmokeOutlineShader
        extends FramebufferShader {
    public float time;
    public static final SmokeOutlineShader INSTANCE;

    public void update(double d) {
        this.time = (float)((double)this.time + d);
    }

    static {
        INSTANCE = new SmokeOutlineShader();
    }

    public void stopDraw(Color color, float f, float f2, boolean bl, int n, float f3, Color color2, Color color3, int n2) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader(color, f, f2, bl, n, f3, color2, color3, n2);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public SmokeOutlineShader() {
        super("smokeOutline.frag");
        this.time = 0.0f;
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("first");
        this.setupUniform("second");
        this.setupUniform("third");
        this.setupUniform("oct");
    }

    public void startShader(Color color, float f, float f2, boolean bl, int n, float f3, Color color2, Color color3, int n2) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(color, f, f2, bl, n, f3, color2, color3, n2);
    }

    public void updateUniforms(Color color, float f, float f2, boolean bl, int n, float f3, Color color2, Color color3, int n2) {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)this.mc.displayWidth * (f * f2)), (float)(1.0f / (float)this.mc.displayHeight * (f * f2)));
        GL20.glUniform1f((int)this.getUniform("divider"), (float)140.0f);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)f);
        GL20.glUniform1f((int)this.getUniform("maxSample"), (float)10.0f);
        GL20.glUniform1f((int)this.getUniform("alpha0"), (float)(bl ? -1.0f : (float)n / 255.0f));
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).getScaledWidth() / f3), (float)((float)new ScaledResolution(this.mc).getScaledHeight() / f3));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform4f((int)this.getUniform("first"), (float)((float)color.getRed() / 255.0f * 5.0f), (float)((float)color.getGreen() / 255.0f * 5.0f), (float)((float)color.getBlue() / 255.0f * 5.0f), (float)((float)color.getAlpha() / 255.0f));
        GL20.glUniform3f((int)this.getUniform("second"), (float)((float)color2.getRed() / 255.0f * 5.0f), (float)((float)color2.getGreen() / 255.0f * 5.0f), (float)((float)color2.getBlue() / 255.0f * 5.0f));
        GL20.glUniform3f((int)this.getUniform("third"), (float)((float)color3.getRed() / 255.0f * 5.0f), (float)((float)color3.getGreen() / 255.0f * 5.0f), (float)((float)color3.getBlue() / 255.0f * 5.0f));
        GL20.glUniform1i((int)this.getUniform("oct"), (int)n2);
    }
}