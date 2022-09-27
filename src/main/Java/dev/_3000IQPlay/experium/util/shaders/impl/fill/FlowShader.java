package dev._3000IQPlay.experium.util.shaders.impl.fill;

import dev._3000IQPlay.experium.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.HashMap;

public class FlowShader
        extends FramebufferShader {
    public static final FlowShader INSTANCE;
    public float time;

    public FlowShader() {
        super("flow.frag");
    }

    public void startShader(float f, float f2, float f3, float f4, float f5, int n, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int n2) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(f, f2, f3, f4, f5, n, f6, f7, f8, f9, f10, f11, f12, f13, n2);
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("color");
        this.setupUniform("iterations");
        this.setupUniform("formuparam2");
        this.setupUniform("stepsize");
        this.setupUniform("volsteps");
        this.setupUniform("zoom");
        this.setupUniform("tile");
        this.setupUniform("distfading");
        this.setupUniform("saturation");
        this.setupUniform("fadeBol");
    }

    static {
        INSTANCE = new FlowShader();
    }

    public void updateUniforms(float f, float f2, float f3, float f4, float f5, int n, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, int n2) {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).getScaledWidth() / f), (float)((float)new ScaledResolution(this.mc).getScaledHeight() / f));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform4f((int)this.getUniform("color"), (float)f2, (float)f3, (float)f4, (float)f5);
        GL20.glUniform1i((int)this.getUniform("iterations"), (int)n);
        GL20.glUniform1f((int)this.getUniform("formuparam2"), (float)f6);
        GL20.glUniform1i((int)this.getUniform("volsteps"), (int)((int)f8));
        GL20.glUniform1f((int)this.getUniform("stepsize"), (float)f9);
        GL20.glUniform1f((int)this.getUniform("zoom"), (float)f7);
        GL20.glUniform1f((int)this.getUniform("tile"), (float)f10);
        GL20.glUniform1f((int)this.getUniform("distfading"), (float)f11);
        GL20.glUniform1f((int)this.getUniform("saturation"), (float)f12);
        GL20.glUniform1i((int)this.getUniform("fadeBol"), (int)n2);
    }

    public void update(double d) {
        this.time = (float)((double)this.time + d);
    }

    public void stopDraw(Color color, float f, float f2, float f3, float f4, float f5, float f6, float f7, int n, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, int n2) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.radius = f;
        this.quality = f2;
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader(f3, f4, f5, f6, f7, n, f8, f9, f10, f11, f12, f13, f14, f15, n2);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}