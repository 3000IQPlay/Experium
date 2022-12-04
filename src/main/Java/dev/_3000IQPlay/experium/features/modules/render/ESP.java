package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.client.Colors;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ESP
        extends Module {
    private static ESP INSTANCE = new ESP();
    public final Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public final Setting<Boolean> items = this.register(new Setting<Boolean>("Items", false));
    public final Setting<Boolean> xporbs = this.register(new Setting<Boolean>("XpOrbs", false));
    public final Setting<Boolean> xpbottles = this.register(new Setting<Boolean>("XpBottles", false));
    public final Setting<Boolean> pearl = this.register(new Setting<Boolean>("Pearls", false));
	public final Setting<Color> espC = this.register(new Setting<Color>("ESPColor", new Color(40, 192, 255, 255)));
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 120, 0, 255));
    public final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));

    public ESP() {
        super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static ESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        if (this.items.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityItem) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 0, (int) 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean) false);
                GL11.glEnable((int) 2848);
                GL11.glHint((int) 3154, (int) 4354);
                GL11.glLineWidth((float) 1.0f);
                RenderGlobal.renderFilledBox((AxisAlignedBB) bb, (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getRed() / 255.0f : (float) this.espC.getValue().getRed() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f : (float) this.espC.getValue().getGreen() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f : (float) this.espC.getValue().getBlue() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getAlpha() : (float) this.boxAlpha.getValue().intValue() / 255.0f));
                GL11.glDisable((int) 2848);
                GlStateManager.depthMask((boolean) true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.espC.getValue().getRed(), this.espC.getValue().getGreen(), this.espC.getValue().getBlue(), this.espC.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
            }
        }
        if (this.xporbs.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityXPOrb) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 0, (int) 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean) false);
                GL11.glEnable((int) 2848);
                GL11.glHint((int) 3154, (int) 4354);
                GL11.glLineWidth((float) 1.0f);
                RenderGlobal.renderFilledBox((AxisAlignedBB) bb, (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getRed() / 255.0f : (float) this.espC.getValue().getRed() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f : (float) this.espC.getValue().getGreen() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f : (float) this.espC.getValue().getBlue() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f : (float) this.boxAlpha.getValue().intValue() / 255.0f));
                GL11.glDisable((int) 2848);
                GlStateManager.depthMask((boolean) true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.espC.getValue().getRed(), this.espC.getValue().getGreen(), this.espC.getValue().getBlue(), this.espC.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
            }
        }
        if (this.pearl.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderPearl) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 0, (int) 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean) false);
                GL11.glEnable((int) 2848);
                GL11.glHint((int) 3154, (int) 4354);
                GL11.glLineWidth((float) 1.0f);
                RenderGlobal.renderFilledBox((AxisAlignedBB) bb, (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getRed() / 255.0f : (float) this.espC.getValue().getRed() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f : (float) this.espC.getValue().getGreen() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f : (float) this.espC.getValue().getBlue() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f : (float) this.boxAlpha.getValue().intValue() / 255.0f));
                GL11.glDisable((int) 2848);
                GlStateManager.depthMask((boolean) true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.espC.getValue().getRed(), this.espC.getValue().getGreen(), this.espC.getValue().getBlue(), this.espC.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
            }
        }
        if (this.xpbottles.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityExpBottle) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 0, (int) 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean) false);
                GL11.glEnable((int) 2848);
                GL11.glHint((int) 3154, (int) 4354);
                GL11.glLineWidth((float) 1.0f);
                RenderGlobal.renderFilledBox((AxisAlignedBB) bb, (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getRed() / 255.0f : (float) this.espC.getValue().getRed() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f : (float) this.espC.getValue().getGreen() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f : (float) this.espC.getValue().getBlue() / 255.0f), (float) (this.colorSync.getValue() != false ? (float) Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f : (float) this.boxAlpha.getValue().intValue() / 255.0f));
                GL11.glDisable((int) 2848);
                GlStateManager.depthMask((boolean) true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(this.espC.getValue().getRed(), this.espC.getValue().getGreen(), this.espC.getValue().getBlue(), this.espC.getValue().getAlpha()), 1.0f);
                if (++i < 50) continue;
            }
        }
    }
}
