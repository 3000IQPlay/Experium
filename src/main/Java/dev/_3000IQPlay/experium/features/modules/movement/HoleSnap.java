package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.Timer;
import dev._3000IQPlay.experium.util.oyveyutils.HoleUtilSafety;
import dev._3000IQPlay.experium.features.modules.movement.SpeedNew;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class HoleSnap
        extends Module {
	Timer timer = new Timer();;
    public HoleUtilSafety.Hole holes;
	public Setting<Mode> mode = this.register(new Setting<Mode>("SnapMode", Mode.Motion));
	private final Setting<Float> range2 = this.register(new Setting<Float>("Motion Range", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), f -> this.mode.getValue() == Mode.Motion));
	public Setting<Float> timerfactor = this.register(new Setting<Float>("Timer", Float.valueOf(2.0f), Float.valueOf(1.0f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Motion));
    private Setting<Boolean> motionstop = this.register(new Setting<Boolean>("StopMotion", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Motion));
	private final Setting<Boolean> SpeedCheck = this.register(new Setting<Boolean>("Disable Speed", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Motion));
	private final Setting<Boolean> StepCheck = this.register(new Setting<Boolean>("Disable Step", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Motion));
    private final Setting<Float> range = this.register(new Setting<Float>("Instant Range", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Instant));
	private int ticks = 0;
	
	public HoleSnap() {
        super("HoleSnap", "Teleport to Hole", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onTick() {
        BlockPos blockPos2;
        if (this.mode.getValue() == Mode.Instant) {
            blockPos2 = Experium.holeManager.calcHoles().stream().min(Comparator.comparing(blockPos -> HoleSnap.mc.player.getDistance((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()))).orElse(null);
            if (blockPos2 != null) {
                if (HoleSnap.mc.player.getDistance((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()) < (double)this.range.getValue().floatValue() + 1.5) {
                    HoleSnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    HoleSnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    Command.sendMessage("Accepting Teleport");
                } else {
                    Command.sendMessage("Out of range. disabling HoleSnap");
                }
            } else {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
            }
            this.disable();
        }
        if (this.mode.getValue() == Mode.Motion) {
            if (HoleSnap.fullNullCheck()) {
                return;
            }
            if (EntityUtil.isInLiquid()) {
                this.disable();
                return;
            }
            HoleSnap.mc.timer.tickLength = 50.0f / this.timerfactor.getValue().floatValue();
            this.holes = HoleSnap.getTargetHoleVec3D(this.range2.getValue().floatValue());
            if (this.holes == null) {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
                this.disable();
                return;
            }
            if (this.timer.passedMs(500L)) {
                this.disable();
                return;
            }
            if (HoleUtilSafety.isObbyHole(HoleSnap.getPlayerPos()) || HoleUtilSafety.isBedrockHoles(HoleSnap.getPlayerPos())) {
                this.disable();
                return;
            }
            if (HoleSnap.mc.world.getBlockState(this.holes.pos1).getBlock() != Blocks.AIR) {
                this.disable();
                return;
            }
            blockPos2 = this.holes.pos1;
            Vec3d vec3d = HoleSnap.mc.player.getPositionVector();
            Vec3d vec3d2 = new Vec3d((double)blockPos2.getX() + 0.5, HoleSnap.mc.player.posY, (double)blockPos2.getZ() + 0.5);
            double d = Math.toRadians(HoleSnap.getRotationTo((Vec3d)vec3d, (Vec3d)vec3d2).x);
            double d2 = vec3d.distanceTo(vec3d2);
            double d3 = HoleSnap.mc.player.onGround ? -Math.min(0.2805, d2 / 2.0) : -EntityUtil.getMaxSpeed() + 0.02;
            HoleSnap.mc.player.motionX = -Math.sin(d) * d3;
            HoleSnap.mc.player.motionZ = Math.cos(d) * d3;
        }
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        this.holes = null;
        HoleSnap.mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Motion && this.motionstop.getValue().booleanValue()) {
            HoleSnap.mc.player.motionX = 0.0;
            HoleSnap.mc.player.motionZ = 0.0;
        }
        if (this.SpeedCheck.getValue().booleanValue() && SpeedNew.getInstance().isOn()) {
            SpeedNew.getInstance().disable();
            Command.sendMessage("<HoleSnap> Disable Speed");
        }
		if (this.StepCheck.getValue().booleanValue() && Step.getInstance().isOn()) {
            Step.getInstance().disable();
            Command.sendMessage("<HoleSnap> Disable Step");
        }
        if (HoleSnap.fullNullCheck()) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }
	
	public static HoleUtilSafety.Hole getTargetHoleVec3D(double d) {
        return HoleUtilSafety.getHoles(d, HoleSnap.getPlayerPos(), false).stream().filter(hole -> HoleSnap.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, HoleSnap.mc.player.posY, (double)hole.pos1.getZ() + 0.5)) <= d).min(Comparator.comparingDouble(hole -> HoleSnap.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, HoleSnap.mc.player.posY, (double)hole.pos1.getZ() + 0.5)))).orElse(null);
    }
	
	public static BlockPos getPlayerPos() {
        double d = HoleSnap.mc.player.posY - Math.floor(HoleSnap.mc.player.posY);
        return new BlockPos(HoleSnap.mc.player.posX, d > 0.8 ? Math.floor(HoleSnap.mc.player.posY) + 1.0 : Math.floor(HoleSnap.mc.player.posY), HoleSnap.mc.player.posZ);
    }
	
	public static Vec2f getRotationTo(Vec3d vec3d, Vec3d vec3d2) {
        return HoleSnap.getRotationFromVec(vec3d.subtract(vec3d2));
    }
	
	public static Vec2f getRotationFromVec(Vec3d vec3d) {
        double d = Math.hypot(vec3d.x, vec3d.z);
        float f = (float)HoleSnap.normalizeAngle(Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0);
        float f2 = (float)HoleSnap.normalizeAngle(Math.toDegrees(-Math.atan2(vec3d.y, d)));
        return new Vec2f(f, f2);
    }
	
	public static double normalizeAngle(Double d) {
        double d2 = 0.0;
        double d3 = d;
        d3 %= 360.0;
        if (d2 >= 180.0) {
            d3 -= 360.0;
        }
        if (d3 < -180.0) {
            d3 += 360.0;
        }
        return d3;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive receive) {
        if (this.isDisabled()) {
            return;
        }
        if (receive.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
            return;
        }
    }

    public static enum Mode {
        Instant,
        Motion;
    }
}
