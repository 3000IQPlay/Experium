package dev._3000IQPlay.experium.features.modules.render;

import java.util.ArrayList;
import java.util.List;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Trajectories
        extends Module {
    private final Setting<Float> size = this.register(new Setting<Float>("Size", Float.valueOf(1.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    private final Setting<Float> innerSize = this.register(new Setting<Float>("Inner Size", Float.valueOf(1.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    public Setting slices = this.register(new Setting<Integer>("Slices", 100, 2, 100));
    public Setting red = this.register(new Setting<Integer>("Red", 135, 0, 255));
    public Setting green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    public Setting blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    public Setting alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));

    public Trajectories() {
        super("Trajectories", "Draws trajectories.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Trajectories.mc.world != null && Trajectories.mc.player != null) {
            mc.getRenderManager();
            double renderPosX = Trajectories.mc.player.lastTickPosX + (Trajectories.mc.player.posX - Trajectories.mc.player.lastTickPosX) * (double) event.getPartialTicks();
            double renderPosY = Trajectories.mc.player.lastTickPosY + (Trajectories.mc.player.posY - Trajectories.mc.player.lastTickPosY) * (double) event.getPartialTicks();
            double renderPosZ = Trajectories.mc.player.lastTickPosZ + (Trajectories.mc.player.posZ - Trajectories.mc.player.lastTickPosZ) * (double) event.getPartialTicks();
            Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (Trajectories.mc.gameSettings.thirdPersonView == 0 && (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEgg || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSnowball || Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle)) {
                float pow;
                GL11.glPushMatrix();
                Item item = Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem();
                double posX = renderPosX - (double) (MathHelper.cos((float) (Trajectories.mc.player.rotationYaw / 180.0f * (float) Math.PI)) * 0.16f);
                double posY = renderPosY + (double) Trajectories.mc.player.getEyeHeight() - 0.1000000014901161;
                double posZ = renderPosZ - (double) (MathHelper.sin((float) (Trajectories.mc.player.rotationYaw / 180.0f * (float) Math.PI)) * 0.16f);
                double motionX = (double) (-MathHelper.sin((float) (Trajectories.mc.player.rotationYaw / 180.0f * (float) Math.PI)) * MathHelper.cos((float) (Trajectories.mc.player.rotationPitch / 180.0f * (float) Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
                double motionY = (double) (-MathHelper.sin((float) (Trajectories.mc.player.rotationPitch / 180.0f * (float) Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
                double motionZ = (double) (MathHelper.cos((float) (Trajectories.mc.player.rotationYaw / 180.0f * (float) Math.PI)) * MathHelper.cos((float) (Trajectories.mc.player.rotationPitch / 180.0f * (float) Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
                int var6 = 72000 - Trajectories.mc.player.getItemInUseCount();
                float power = (float) var6 / 20.0f;
                power = (power * power + power * 2.0f) / 3.0f;
                if (power > 1.0f) {
                    power = 1.0f;
                }
                float distance = MathHelper.sqrt((double) (motionX * motionX + motionY * motionY + motionZ * motionZ));
                motionX /= (double) distance;
                motionY /= (double) distance;
                motionZ /= (double) distance;
                float f = item instanceof ItemBow ? power * 2.0f : (item instanceof ItemFishingRod ? 1.25f : (pow = Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.9f : 1.0f));
                motionX *= (double) (power * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
                motionY *= (double) (power * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
                motionZ *= (double) (power * (item instanceof ItemFishingRod ? 0.75f : (Trajectories.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
                this.enableGL3D(2.0f);
                GlStateManager.color((float) ((float) ((Integer) this.red.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.green.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.blue.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.alpha.getValue()).intValue() / 255.0f));
                GL11.glEnable((int) 2848);
                float size = (float) (item instanceof ItemBow ? 0.3 : 0.25);
                boolean hasLanded = false;
                Entity landingOnEntity = null;
                RayTraceResult landingPosition = null;
                while (!hasLanded && posY > 0.0) {
                    Vec3d present = new Vec3d(posX, posY, posZ);
                    Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                    RayTraceResult possibleLandingStrip = Trajectories.mc.world.rayTraceBlocks(present, future, false, true, false);
                    if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                        landingPosition = possibleLandingStrip;
                        hasLanded = true;
                    }
                    AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double) size, posY - (double) size, posZ - (double) size, posX + (double) size, posY + (double) size, posZ + (double) size);
                    List<Entity> entities = this.getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                    for (Entity entity : entities) {
                        AxisAlignedBB var8;
                        RayTraceResult possibleEntityLanding;
                        if (!entity.canBeCollidedWith() || entity == Trajectories.mc.player || (possibleEntityLanding = (var8 = entity.getEntityBoundingBox().expand((double) 0.3f, (double) 0.3f, (double) 0.3f)).calculateIntercept(present, future)) == null)
                            continue;
                        hasLanded = true;
                        landingOnEntity = entity;
                        landingPosition = possibleEntityLanding;
                    }
                    if (landingOnEntity != null) {
                        GlStateManager.color((float) ((float) ((Integer) this.red.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.green.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.blue.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.alpha.getValue()).intValue() / 255.0f));
                    }
                    motionY *= (double) 0.99f;
                    this.drawLine3D((posX += (motionX *= (double) 0.99f)) - renderPosX, (posY += (motionY -= item instanceof ItemBow ? 0.05 : 0.03)) - renderPosY, (posZ += (motionZ *= (double) 0.99f)) - renderPosZ);
                }
                if (landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
                    GlStateManager.translate((double) (posX - renderPosX), (double) (posY - renderPosY), (double) (posZ - renderPosZ));
                    int side = landingPosition.sideHit.getIndex();
                    if (side == 2) {
                        GlStateManager.rotate((float) 90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
                    } else if (side == 3) {
                        GlStateManager.rotate((float) 90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
                    } else if (side == 4) {
                        GlStateManager.rotate((float) 90.0f, (float) 0.0f, (float) 0.0f, (float) 1.0f);
                    } else if (side == 5) {
                        GlStateManager.rotate((float) 90.0f, (float) 0.0f, (float) 0.0f, (float) 1.0f);
                    }
                    Cylinder c = new Cylinder();
                    GlStateManager.rotate((float) -90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
                    c.setDrawStyle(100011);
                    if (landingOnEntity != null) {
                        GlStateManager.color((float) ((float) ((Integer) this.red.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.green.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.blue.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.alpha.getValue()).intValue() / 255.0f));
                        GL11.glLineWidth((float) 2.5f);
                        c.draw(0.5f, 0.5f, 0.0f, ((Integer) this.slices.getValue()).intValue(), 1);
                        GL11.glLineWidth((float) 0.1f);
                        GlStateManager.color((float) ((float) ((Integer) this.red.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.green.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.blue.getValue()).intValue() / 255.0f), (float) ((float) ((Integer) this.alpha.getValue()).intValue() / 255.0f));
                    }
                    c.draw(this.size.getValue().floatValue(), this.innerSize.getValue().floatValue(), 0.0f, ((Integer) this.slices.getValue()).intValue(), 1);
                }
                this.disableGL3D();
                GL11.glPopMatrix();
            }
        }
    }

    public void enableGL3D(float lineWidth) {
        GL11.glDisable((int) 3008);
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDisable((int) 3553);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glEnable((int) 2884);
        Trajectories.mc.entityRenderer.disableLightmap();
        GL11.glEnable((int) 2848);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
        GL11.glLineWidth((float) lineWidth);
    }

    public void disableGL3D() {
        GL11.glEnable((int) 3553);
        GL11.glEnable((int) 2929);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 3008);
        GL11.glDepthMask((boolean) true);
        GL11.glCullFace((int) 1029);
        GL11.glDisable((int) 2848);
        GL11.glHint((int) 3154, (int) 4352);
        GL11.glHint((int) 3155, (int) 4352);
    }

    public void drawLine3D(double var1, double var2, double var3) {
        GL11.glVertex3d((double) var1, (double) var2, (double) var3);
    }

    private List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = MathHelper.floor((double) ((bb.minX - 2.0) / 16.0));
        int chunkMaxX = MathHelper.floor((double) ((bb.maxX + 2.0) / 16.0));
        int chunkMinZ = MathHelper.floor((double) ((bb.minZ - 2.0) / 16.0));
        int chunkMaxZ = MathHelper.floor((double) ((bb.maxZ + 2.0) / 16.0));
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.world.getChunkProvider().getLoadedChunk(x, z) == null) continue;
                Trajectories.mc.world.getChunk(x, z).getEntitiesWithinAABBForEntity((Entity) Trajectories.mc.player, bb, list, null);
            }
        }
        return list;
    }
}
