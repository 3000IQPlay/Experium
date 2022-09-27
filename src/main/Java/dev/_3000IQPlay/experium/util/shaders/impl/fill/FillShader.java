package dev._3000IQPlay.experium.util.shaders.impl.fill;

import dev._3000IQPlay.experium.util.shaders.FramebufferShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.HashMap;

public class FillShader
        extends FramebufferShader {
    public float time;
    public static final FillShader INSTANCE;

    public void update(double d) {
        this.time = (float)((double)this.time + d);
    }

    public FillShader() {
        super("fill.frag");
    }

    static {
        INSTANCE = new FillShader();
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("color");
    }

    public void startShader(float f, float f2, float f3, float f4) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(f, f2, f3, f4);
    }

    public void stopDraw(Color color) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void updateUniforms(float f, float f2, float f3, float f4) {
        GL20.glUniform4f((int)this.getUniform("color"), (float)f, (float)f2, (float)f3, (float)f4);
    }
}