package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ChinaHat
        extends Module {
    public Setting<Float> height = this.register(new Setting<Float>("Height", 0.3f, 0.1f, 1.0f));
    public Setting<Float> radius = this.register(new Setting<Float>("Radius", 0.7f, 0.3f, 1.5f));
    public Setting<Float> yPos = this.register(new Setting<Float>("YPos", 0.0f, -1.0f, 1.0f));
    public Setting<Boolean> drawThePlayer = this.register(new Setting<Boolean>("DrawThePlayer", true));
    public Setting<Boolean> onlyThirdPerson = this.register(new Setting<Boolean>("OnlyThirdPerson", true, v -> this.drawThePlayer.getValue()));
	public Setting<Color> colorC = this.register(new Setting<Color>("HatColor", new Color(40, 192, 255, 255)));
	
	public ChinaHat() {
        super("ChinaHat", "Better shit", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(drawThePlayer.getValue().booleanValue() && !(onlyThirdPerson.getValue().booleanValue() && mc.gameSettings.thirdPersonView == 0)) {
            drawChinaHatFor(mc.player);
        }
    }
    
    public void drawChinaHatFor(EntityLivingBase entity) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4f(this.colorC.getValue().getRed() / 255.0f, this.colorC.getValue().getGreen() / 255.0f, this.colorC.getValue().getBlue() / 255.0f, this.colorC.getValue().getAlpha() / 255.0f);
        GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY + entity.height + yPos.getValue().floatValue(),
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3d(0.0, height.getValue().floatValue(), 0.0);
        float radius = this.radius.getValue();
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex3d(Math.cos((double)i * Math.PI / 180.0) * radius, 0.0, Math.sin((double)i * Math.PI / 180.0) * radius);
        }
        GL11.glVertex3d(0.0, this.height.getValue().floatValue(), 0.0);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.resetColor();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
