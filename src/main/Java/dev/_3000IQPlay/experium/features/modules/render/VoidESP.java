package dev._3000IQPlay.experium.features.modules.render;

import dev._3000IQPlay.experium.event.events.Render3DEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class VoidESP
        extends Module {
    private final Setting<Float> radius = this.register(new Setting<Float>("Radius", Float.valueOf(8.0f), Float.valueOf(0.0f), Float.valueOf(50.0f)));
    private final Timer timer = new Timer();
    private final Setting<Integer> updates = this.register(new Setting<Integer>("Updates", 500, 0, 1000));
    private final Setting<Integer> voidCap = this.register(new Setting<Integer>("VoidCap", 500, 0, 1000));
	private final Setting<Color> colorC = this.register(new Setting<Color>("BoxColor", new Color(40, 192, 255, 40)));
    public Setting<Boolean> air = this.register(new Setting<Boolean>("OnlyAir", true));
    public Setting<Boolean> noEnd = this.register(new Setting<Boolean>("NoEnd", true));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", false));
    public Setting<Double> height = this.register(new Setting<Double>("Height", 0.0, -2.0, 2.0));
    public Setting<Boolean> customOutline = this.register(new Setting<Object>("CustomLine", Boolean.valueOf(false), v -> this.outline.getValue()));
	private final Setting<Color> lineC = this.register(new Setting<Color>("OutlineColor", new Color(40, 192, 255, 255), v -> this.customOutline.getValue() != false && this.outline.getValue() != false));
    private List<BlockPos> voidHoles = new CopyOnWriteArrayList<BlockPos>();

    public VoidESP() {
        super("VoidEsp", "Esps the void", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onToggle() {
        this.timer.reset();
    }

    @Override
    public void onLogin() {
        this.timer.reset();
    }

    @Override
    public void onTick() {
        if (!(VoidESP.fullNullCheck() || this.noEnd.getValue().booleanValue() && VoidESP.mc.player.dimension == 1 || !this.timer.passedMs(this.updates.getValue().intValue()))) {
            this.voidHoles.clear();
            this.voidHoles = this.findVoidHoles();
            if (this.voidHoles.size() > this.voidCap.getValue()) {
                this.voidHoles.clear();
            }
            this.timer.reset();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (VoidESP.fullNullCheck() || this.noEnd.getValue().booleanValue() && VoidESP.mc.player.dimension == 1) {
            return;
        }
        for (BlockPos pos : this.voidHoles) {
            if (!RotationUtil.isInFov(pos)) continue;
            RenderUtil.drawBoxESP(pos, new Color(this.colorC.getValue().getRed(), this.colorC.getValue().getGreen(), this.colorC.getValue().getBlue(), this.colorC.getValue().getAlpha()), this.customOutline.getValue(), new Color(this.lineC.getValue().getRed(), this.lineC.getValue().getGreen(), this.lineC.getValue().getBlue(), this.lineC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), false, false, false, false, 0);
        }
    }

    private List<BlockPos> findVoidHoles() {
        BlockPos playerPos = EntityUtil.getPlayerPos(VoidESP.mc.player);
        return BlockUtil.getDisc(playerPos.add(0, -playerPos.getY(), 0), this.radius.getValue().floatValue()).stream().filter(this::isVoid).collect(Collectors.toList());
    }

    private boolean isVoid(BlockPos pos) {
        return (VoidESP.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || this.air.getValue() == false && VoidESP.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) && pos.getY() < 1 && pos.getY() >= 0;
    }
}

