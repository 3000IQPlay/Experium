package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class KillEffect
        extends Module {
    public Setting<Boolean> thunder = this.register(new Setting<Boolean>("Thunder", true));
    public Setting<Integer> numbersThunder = this.register(new Setting<Integer>("Number Thunder", 1, 1, 10));
    public Setting<Boolean> sound = this.register(new Setting<Boolean>("Sound", true));
    public Setting<Integer> numberSound = this.register(new Setting<Integer>("Number Sound", 1, 1, 10));
    public Setting<Integer> timeActive = this.register(new Setting<Integer>("TimeActive", 20, 0, 50));
    public Setting<Boolean> lightning = this.register(new Setting<Boolean>("lightning", true));
    public Setting<Boolean> totemPop = this.register(new Setting<Boolean>("TotemPop", true));
    public Setting<Boolean> totemPopSound = this.register(new Setting<Boolean>("TotemPopSound", false));
    public Setting<Boolean> firework = this.register(new Setting<Boolean>("FireWork", false));
    public Setting<Boolean> fire = this.register(new Setting<Boolean>("Fire", false));
    public Setting<Boolean> water = this.register(new Setting<Boolean>("Water", false));
    public Setting<Boolean> smoke = this.register(new Setting<Boolean>("Smoke", false));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", true));
    public Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", true));
    public Setting<Boolean> all = this.register(new Setting<Boolean>("All", true));
    ArrayList<EntityPlayer> playersDead = new ArrayList();
    final Object sync = new Object();

    public KillEffect() {
        super("KillEffect", "When you kill something it spawns shit.", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onEnable() {
        this.playersDead.clear();
    }

    @Override
    public void onUpdate() {
        if (KillEffect.mc.world == null) {
            this.playersDead.clear();
            return;
        }
        KillEffect.mc.world.playerEntities.forEach(entity -> {
            if (this.playersDead.contains(entity)) {
                if (entity.getHealth() > 0.0f) {
                    this.playersDead.remove(entity);
                }
            } else if (entity.getHealth() == 0.0f) {
                int i;
                if (this.thunder.getValue().booleanValue()) {
                    for (i = 0; i < this.numbersThunder.getValue(); ++i) {
                        KillEffect.mc.world.spawnEntity((Entity)new EntityLightningBolt((World)KillEffect.mc.world, entity.posX, entity.posY, entity.posZ, true));
                    }
                }
                if (this.sound.getValue().booleanValue()) {
                    for (i = 0; i < this.numberSound.getValue(); ++i) {
                        KillEffect.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                    }
                }
                this.playersDead.add((EntityPlayer)entity);
            }
        });
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() == KillEffect.mc.player) {
            return;
        }
        if (this.shouldRenderParticle(event.getEntity())) {
            if (this.lightning.getValue().booleanValue()) {
                KillEffect.mc.world.addEntityToWorld(-999, (Entity)new EntityLightningBolt((World)KillEffect.mc.world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, true));
            }
            if (this.totemPop.getValue().booleanValue()) {
                this.totemPop(event.getEntity());
            }
            if (this.firework.getValue().booleanValue()) {
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.FIREWORKS_SPARK, this.timeActive.getValue().intValue());
            }
            if (this.fire.getValue().booleanValue()) {
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.FLAME, this.timeActive.getValue().intValue());
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.DRIP_LAVA, this.timeActive.getValue().intValue());
            }
            if (this.water.getValue().booleanValue()) {
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.WATER_BUBBLE, this.timeActive.getValue().intValue());
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.WATER_DROP, this.timeActive.getValue().intValue());
            }
            if (this.smoke.getValue().booleanValue()) {
                KillEffect.mc.effectRenderer.emitParticleAtEntity(event.getEntity(), EnumParticleTypes.SMOKE_NORMAL, this.timeActive.getValue().intValue());
            }
        }
    }

    public boolean shouldRenderParticle(Entity entity) {
        return entity != KillEffect.mc.player && (this.all.getValue() != false || entity instanceof EntityPlayer && this.players.getValue() != false || entity instanceof EntityMob || entity instanceof EntitySlime && this.mobs.getValue() != false || entity instanceof EntityAnimal && this.animals.getValue() != false);
    }

    public void totemPop(Entity entity) {
        KillEffect.mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, this.timeActive.getValue().intValue());
        if (this.totemPopSound.getValue().booleanValue()) {
            KillEffect.mc.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
        }
    }
}

