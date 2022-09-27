package dev._3000IQPlay.experium.util.shaders.impl.fill;

import dev._3000IQPlay.experium.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.HashMap;

public class RainbowCubeShader
        extends FramebufferShader {
    public static final RainbowCubeShader INSTANCE;
    public float time;

    public void updateUniforms(float f, Color color, int n, int n2, int n3, int n4) {
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)((float)new ScaledResolution(this.mc).getScaledWidth() / f), (float)((float)new ScaledResolution(this.mc).getScaledHeight() / f));
        GL20.glUniform1f((int)this.getUniform("time"), (float)this.time);
        GL20.glUniform1f((int)this.getUniform("alpha"), (float)((float)color.getAlpha() / 255.0f));
        GL20.glUniform1f((int)this.getUniform("WAVELENGTH"), (float)n);
        GL20.glUniform1i((int)this.getUniform("R"), (int)color.getRed());
        GL20.glUniform1i((int)this.getUniform("G"), (int)color.getGreen());
        GL20.glUniform1i((int)this.getUniform("B"), (int)color.getBlue());
        GL20.glUniform1i((int)this.getUniform("RSTART"), (int)n2);
        GL20.glUniform1i((int)this.getUniform("GSTART"), (int)n3);
        GL20.glUniform1i((int)this.getUniform("BSTART"), (int)n4);
    }

    public void update(double d) {
        this.time = (float)((double)this.time + d);
    }

    public void stopDraw(Color color, float f, float f2, float f3, Color color2, int n, int n2, int n3, int n4) {
        this.mc.gameSettings.entityShadows = this.entityShadows;
        this.framebuffer.unbindFramebuffer();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.mc.getFramebuffer().bindFramebuffer(true);
        this.red = (float)color.getRed() / 255.0f;
        this.green = (float)color.getGreen() / 255.0f;
        this.blue = (float)color.getBlue() / 255.0f;
        this.alpha = (float)color.getAlpha() / 255.0f;
        this.radius = f;
        this.quality = f2;
        this.mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        this.startShader(f3, color2, n, n2, n3, n4);
        this.mc.entityRenderer.setupOverlayRendering();
        this.drawFramebuffer(this.framebuffer);
        this.stopShader();
        this.mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
        this.setupUniform("alpha");
        this.setupUniform("WAVELENGTH");
        this.setupUniform("R");
        this.setupUniform("G");
        this.setupUniform("B");
        this.setupUniform("RSTART");
        this.setupUniform("GSTART");
        this.setupUniform("BSTART");
    }

    public void startShader(float f, Color color, int n, int n2, int n3, int n4) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap();
            this.setupUniforms();
        }
        this.updateUniforms(f, color, n, n2, n3, n4);
    }

    static {
        INSTANCE = new RainbowCubeShader();
    }

    public RainbowCubeShader() {
        super("rainbowCube.frag");
    }
}