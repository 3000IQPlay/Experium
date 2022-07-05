package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.ColorUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.Color;

public class BurrowESP
        extends Module {
    private final Setting<Integer> range = this.register(new Setting<Integer>("Range", 10, 0, 20));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    public Setting<Boolean> gradientBox = this.register(new Setting<Object>("Gradient", Boolean.valueOf(false), v -> this.box.getValue()));
    public Setting<Boolean> invertGradientBox = this.register(new Setting<Object>("ReverseGradient", Boolean.valueOf(false), v -> this.gradientBox.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    public Setting<Boolean> gradientOutline = this.register(new Setting<Object>("GradientOutline", Boolean.valueOf(false), v -> this.outline.getValue()));
    public Setting<Boolean> invertGradientOutline = this.register(new Setting<Object>("ReverseOutline", Boolean.valueOf(false), v -> this.gradientOutline.getValue()));
    public Setting<Double> height = this.register(new Setting<Double>("Height", 0.0, -2.0, 2.0));
    private Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public Setting<Boolean> safeColor = this.register(new Setting<Boolean>("BedrockColor", false));
    private Setting<Integer> safeRed = this.register(new Setting<Object>("BedrockRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private Setting<Integer> safeGreen = this.register(new Setting<Object>("BedrockGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private Setting<Integer> safeBlue = this.register(new Setting<Object>("BedrockBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    private Setting<Integer> safeAlpha = this.register(new Setting<Object>("BedrockAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.safeColor.getValue()));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.outline.getValue()));
    private Setting<Integer> safecRed = this.register(new Setting<Object>("OutlineSafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private Setting<Integer> safecGreen = this.register(new Setting<Object>("OutlineSafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private Setting<Integer> safecBlue = this.register(new Setting<Object>("OutlineSafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));
    private Setting<Integer> safecAlpha = this.register(new Setting<Object>("OutlineSafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false && this.safeColor.getValue() != false));

    public BurrowESP() {
        super("BurrowESP", "Shows you 8yo kids", Module.Category.RENDER, false, false, false);
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : BurrowESP.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)player, range) || Experium.speedManager.getPlayerSpeed(player) > 10.0) continue;
            if (target == null) {
                target = player;
                distance = BurrowESP.mc.player.getDistanceSq((Entity)player);
                continue;
            }
            if (!(BurrowESP.mc.player.getDistanceSq((Entity)player) < distance)) continue;
            target = player;
            distance = BurrowESP.mc.player.getDistanceSq((Entity)player);
        }
        return target;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        EntityPlayer pss = this.getTarget(this.range.getValue().intValue());
        if (pss != null) {
            if (BurrowESP.mc.world.getBlockState(new BlockPos(pss.posX, pss.posY, pss.posZ)).getBlock() == Blocks.AIR || BurrowESP.mc.world.getBlockState(new BlockPos(pss.posX, pss.posY, pss.posZ)).getBlock() == Blocks.WATER || BurrowESP.mc.world.getBlockState(new BlockPos(pss.posX, pss.posY, pss.posZ)).getBlock() == Blocks.LAVA) {
                pss = null;
                return;
            }
            RenderUtil.drawBoxESP(new BlockPos(pss.posX, pss.posY, pss.posZ), new Color(this.safeRed.getValue(), this.safeGreen.getValue(), this.safeBlue.getValue(), this.safeAlpha.getValue()), this.customOutline.getValue(), new Color(this.safecRed.getValue(), this.safecGreen.getValue(), this.safecBlue.getValue(), this.safecAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0);
        }
    }
}

