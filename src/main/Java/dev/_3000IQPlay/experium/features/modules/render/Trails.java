package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Trails
        extends Module {
    private final Setting<Float> thick = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Boolean> pearl = this.register(new Setting<Boolean>("Pearl", true));
    private final Setting<Boolean> arrows = this.register(new Setting<Boolean>("Arrows", true));
    private final Setting<Boolean> exp = this.register(new Setting<Boolean>("Experience Bottles", true));
    private final Setting<Boolean> potions = this.register(new Setting<Boolean>("Splash Potions", true));
    private final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    private final Setting<Double> aliveTime = this.register(new Setting<Double>("Fade Time", 5.0, 0.0, 20.0));
    private final Setting<Integer> rDelay = this.register(new Setting<Integer>("Delay Before Render", 120, 0, 360));
	private final Setting<Color> colorC = this.register(new Setting<Color>("Color", new Color(40, 192, 255, 255)));
    private final HashMap<UUID, List<Vec3d>> poses = new HashMap();
    private final HashMap<UUID, Double> time = new HashMap();

    public Trails() {
        super("Trails", "Draws a line behind projectiles", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        List<Vec3d> v;
        UUID toRemove = null;
        for (UUID uuid : this.time.keySet()) {
            if (this.time.get(uuid) <= 0.0) {
                this.poses.remove(uuid);
                toRemove = uuid;
                continue;
            }
            this.time.replace(uuid, this.time.get(uuid) - 0.05);
        }
        if (toRemove != null) {
            this.time.remove(toRemove);
            toRemove = null;
        }
        if (this.arrows.getValue().booleanValue() || this.exp.getValue().booleanValue() || this.pearl.getValue().booleanValue() || this.potions.getValue().booleanValue()) {
            for (Entity e : Trails.mc.world.getLoadedEntityList()) {
                if (!(e instanceof EntityArrow) && !(e instanceof EntityExpBottle) && !(e instanceof EntityPotion))
                    continue;
                if (!this.poses.containsKey(e.getUniqueID())) {
                    this.poses.put(e.getUniqueID(), new ArrayList<Vec3d>(Collections.singletonList(e.getPositionVector())));
                    this.time.put(e.getUniqueID(), 0.05);
                    continue;
                }
                this.time.replace(e.getUniqueID(), 0.05);
                v = this.poses.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }
        for (Entity e : Trails.mc.world.getLoadedEntityList()) {
            if (!(e instanceof EntityEnderPearl)) continue;
            if (!this.poses.containsKey(e.getUniqueID())) {
                this.poses.put(e.getUniqueID(), new ArrayList<Vec3d>(Collections.singletonList(e.getPositionVector())));
                this.time.put(e.getUniqueID(), this.aliveTime.getValue());
                continue;
            }
            this.time.replace(e.getUniqueID(), this.aliveTime.getValue());
            v = this.poses.get(e.getUniqueID());
            v.add(e.getPositionVector());
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!this.render.getValue().booleanValue() && !this.poses.isEmpty()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int) 3042);
        GL11.glDisable((int) 3553);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glLineWidth((float) this.thick.getValue().floatValue());
        for (UUID uuid : this.poses.keySet()) {
            if (this.poses.get(uuid).size() <= 2) continue;
            int delay = 0;
            GL11.glBegin((int) 1);
            for (int i = 1; i < this.poses.get(uuid).size(); ++i) {
                delay += this.rDelay.getValue().intValue();
                GL11.glColor4d((double) ((float) this.colorC.getValue().getRed() / 255.0f), (double) ((float) this.colorC.getValue().getGreen() / 255.0f), (double) ((float) this.colorC.getValue().getBlue() / 255.0f), (double) ((float) this.colorC.getValue().getAlpha() / 255.0f));
                List<Vec3d> pos = this.poses.get(uuid);
                GL11.glVertex3d((double) (pos.get((int) i).x - Trails.mc.getRenderManager().viewerPosX), (double) (pos.get((int) i).y - Trails.mc.getRenderManager().viewerPosY), (double) (pos.get((int) i).z - Trails.mc.getRenderManager().viewerPosZ));
                GL11.glVertex3d((double) (pos.get((int) (i - 1)).x - Trails.mc.getRenderManager().viewerPosX), (double) (pos.get((int) (i - 1)).y - Trails.mc.getRenderManager().viewerPosY), (double) (pos.get((int) (i - 1)).z - Trails.mc.getRenderManager().viewerPosZ));
            }
            GL11.glEnd();
        }
        GL11.glEnable((int) 3553);
        GL11.glEnable((int) 2929);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glPopMatrix();
    }
}
