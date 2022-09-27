package dev._3000IQPlay.experium.util.shaders.impl.outline;

import dev._3000IQPlay.experium.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.HashMap;

public final class AstralOutlineShader
        extends FramebufferShader {
    public static final AstralOutlineShader INSTANCE;
    public float time;

    public void update(double d) {
        this.time = (float)((double)this.time + d);
    }

    public void startShader(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5, float f6, float f7, int n2, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, int n3) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(color, f, f2, bl, n, f3, f4, f5, f6, f7, n2, f8, f9, f10, f11, f12, f13, f14, f15, n3);
    }

    public void stopDraw(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5, float f6, float f7, int n2, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, int n3) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader(color, f, f2, bl, n, f3, f4, f5, f6, f7, n2, f8, f9, f10, f11, f12, f13, f14, f15, n3);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    static {
        INSTANCE = new AstralOutlineShader();
    }

    public AstralOutlineShader() {
        super("astralOutline.frag");
        this.time = 0.0f;
    }

    public void updateUniforms(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5, float f6, float f7, int n2, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, int n3) {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)this.mc.displayWidth * (f * f2)), (float)(1.0f / (float)this.mc.displayHeight * (f * f2)));
        GL20.glUniform1f((int)this.getUniform("divider"), (float)140.0f);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)f);
        GL20.glUniform1f((int)this.getUniform("maxSample"), (float)10.0f);
        GL20.glUniform1f((int)this.getUniform("alpha0"), (float)(bl ? -1.0f : (float)n / 255.0f));
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).getScaledWidth() / f3), (float)((float)new ScaledResolution(this.mc).getScaledHeight() / f3));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform4f((int)this.getUniform("color"), (float)f4, (float)f5, (float)f6, (float)f7);
        GL20.glUniform1i((int)this.getUniform("iterations"), (int)n2);
        GL20.glUniform1f((int)this.getUniform("formuparam2"), (float)f8);
        GL20.glUniform1i((int)this.getUniform("volsteps"), (int)((int)f10));
        GL20.glUniform1f((int)this.getUniform("stepsize"), (float)f11);
        GL20.glUniform1f((int)this.getUniform("zoom"), (float)f9);
        GL20.glUniform1f((int)this.getUniform("tile"), (float)f12);
        GL20.glUniform1f((int)this.getUniform("distfading"), (float)f13);
        GL20.glUniform1f((int)this.getUniform("saturation"), (float)f14);
        GL20.glUniform1i((int)this.getUniform("fadeBol"), (int)n3);
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
        this.setupUniform("alpha0");
        this.setupUniform("time");
        this.setupUniform("iterations");
        this.setupUniform("formuparam2");
        this.setupUniform("stepsize");
        this.setupUniform("volsteps");
        this.setupUniform("zoom");
        this.setupUniform("tile");
        this.setupUniform("distfading");
        this.setupUniform("saturation");
        this.setupUniform("fadeBol");
        this.setupUniform("resolution");
    }
}