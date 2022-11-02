package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.MoveEvent;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.movement.Strafe;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.manager.ModuleManager;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class SpeedNew
        extends Module {
	private static SpeedNew instance;
    private final Setting<SpeedNewModes> mode = this.register(new Setting<SpeedNewModes>("Mode", SpeedNewModes.Custom));
	private final Setting<Float> yPortAirSpeed = this.register(new Setting<Float>("YPortAirSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortGroundSpeed = this.register(new Setting<Float>("YPortGroundSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortJumpMotionY = this.register(new Setting<Float>("YPortJumpMotionY", 0.42f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortFallSpeed = this.register(new Setting<Float>("FallSpeed", 1.0f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Boolean> yPortTimerSpeed = this.register(new Setting<Boolean>("Timer", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	private final Setting<Float> yPortTimerSpeedVal = this.register(new Setting<Float>("TimerSpeed", 1.8f, 0.1f, 5.0f, t -> this.yPortTimerSpeed.getValue() && this.mode.getValue().equals((Object)SpeedNewModes.YPort)));
	
	private final Setting<Float> cpvpccSpeed = this.register(new Setting<Float>("TestSpeed", 0.435f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.CPVPCC)));
	
    private final Setting<Float> upAirSpeed = this.register(new Setting<Float>("UpAirSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downAirSpeed = this.register(new Setting<Float>("DownAirSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> onGroundSpeed = this.register(new Setting<Float>("GroundSpeed", 0.35f, 0.2f, 5.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Boolean> autoJump = this.register(new Setting<Boolean>("AutoJump", true, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Float> jumpMotionY = this.register(new Setting<Float>("JumpMotionY", 0.42f, 0.0f, 4.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<DownMode> downMode = this.register(new Setting<DownMode>("DownType", DownMode.Timer, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downTimerValue = this.register(new Setting<Float>("CustomDownTimer", 1.0f, 0.1f, 3.0f, t -> this.downMode.getValue() == DownMode.Timer && this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> downMotionValue = this.register(new Setting<Float>("CustomDownMotion", 0.2f, 0.0f, 3.0f, t -> this.downMode.getValue() == DownMode.Motion && this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
	private final Setting<Float> upTimerValue = this.register(new Setting<Float>("CustomUpTimer", 1.0f, 0.1f, 3.0f, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Boolean> resetXZ = this.register(new Setting<Boolean>("ResetXZ", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private final Setting<Boolean> resetY = this.register(new Setting<Boolean>("ResetY", false, t -> this.mode.getValue().equals((Object)SpeedNewModes.Custom)));
    private double lastDist;
    private double moveSpeedNew;
	private int airDance;
    int stage;

    public SpeedNew() {
        super("SpeedNew", "placeholder", Module.Category.MOVEMENT, false, false, false);
		instance = this;
    }
	
	public static SpeedNew getInstance() {
        if (instance == null) {
            instance = new SpeedNew();
        }
        return instance;
    }

    @SubscribeEvent
    public void onStrafe(MoveEvent event) {
        if (Strafe.fullNullCheck()) {
            return;
        }
        if (Strafe.mc.player.isInWater() || Strafe.mc.player.isInLava()) {
            return;
		}
        if (Strafe.mc.player.onGround) {
            this.stage = 2;
        }
        switch (this.stage) {
            case 0: {
                ++this.stage;
                this.lastDist = 0.0;
                break;
            }
            case 2: {
                double motionY = 0.40123128;
                if (!Strafe.mc.player.onGround || !Strafe.mc.gameSettings.keyBindJump.isKeyDown()) {
			        break;
				}
                if (Strafe.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (double)((float)(Strafe.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f);
                }
                Strafe.mc.player.motionY = motionY;
                event.setY(Strafe.mc.player.motionY);
                this.moveSpeedNew *= 2.149;
                break;
            }
            case 3: {
                this.moveSpeedNew = this.lastDist - 0.795 * (this.lastDist - this.getBaseMoveSpeedNew());
                break;
            }
            default: {
                if ((Strafe.mc.world.getCollisionBoxes((Entity)Strafe.mc.player, Strafe.mc.player.getEntityBoundingBox().offset(0.0, Strafe.mc.player.motionY, 0.0)).size() > 0 || Strafe.mc.player.collidedVertically) && this.stage > 0) {
                    this.stage = Strafe.mc.player.moveForward != 0.0f || Strafe.mc.player.moveStrafing != 0.0f ? 1 : 0;
                }
                this.moveSpeedNew = this.lastDist - this.lastDist / 159.0;
            }
        }
        this.moveSpeedNew = !Strafe.mc.gameSettings.keyBindJump.isKeyDown() && Strafe.mc.player.onGround ? this.getBaseMoveSpeedNew() : Math.max(this.moveSpeedNew, this.getBaseMoveSpeedNew());
        double n = Strafe.mc.player.movementInput.moveForward;
        double n2 = Strafe.mc.player.movementInput.moveStrafe;
        double n3 = Strafe.mc.player.rotationYaw;
        if (n == 0.0 && n2 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (n != 0.0 && n2 != 0.0) {
            n *= Math.sin(0.7853981633974483);
            n2 *= Math.cos(0.7853981633974483);
        }
        double n4 = 0.99;
        event.setX((n * this.moveSpeedNew * -Math.sin(Math.toRadians(n3)) + n2 * this.moveSpeedNew * Math.cos(Math.toRadians(n3))) * n4);
        event.setZ((n * this.moveSpeedNew * Math.cos(Math.toRadians(n3)) - n2 * this.moveSpeedNew * -Math.sin(Math.toRadians(n3))) * n4);
        ++this.stage;
        event.setCanceled(true);
    }

    public double getBaseMoveSpeedNew() {
        double n = 0.2873;
        if (!Strafe.mc.player.isPotionActive(MobEffects.SPEED)) {
            return n;
        }
        return n *= 1.0 + 0.2 * (double)(Objects.requireNonNull(Strafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
    }

    @SubscribeEvent
    public void onMotion(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1 || Strafe.fullNullCheck()) {
            return;
        }
		switch (this.mode.getValue()) {
            case Custom: {
                if (MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player)) {
                    if (SpeedNew.mc.player.onGround) {
                        EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                        if (this.autoJump.getValue().booleanValue()) {
                            SpeedNew.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (SpeedNew.mc.player.motionY > 0) {
						Experium.timerManager.setTimer(this.upTimerValue.getValue());
						EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                        break;
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Experium.timerManager.setTimer(this.downTimerValue.getValue().floatValue());
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							SpeedNew.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                        break;
			        }
                }
                SpeedNew.mc.player.motionX = SpeedNew.mc.player.motionZ = 0.0;
                break;
            }
			case YPort: {
                if (!MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player) || SpeedNew.mc.player.collidedHorizontally) {
                    return;
                }
				if (this.yPortTimerSpeed.getValue().booleanValue()) {
					Experium.timerManager.setTimer(this.yPortTimerSpeedVal.getValue().floatValue());
				} else {
					Experium.timerManager.reset();
				}
				if (SpeedNew.mc.player.onGround) {
                    SpeedNew.mc.player.motionY = this.yPortJumpMotionY.getValue().floatValue();
                    EntityUtil.moveEntityStrafe(this.yPortGroundSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                } else {
                    SpeedNew.mc.player.motionY =- this.yPortFallSpeed.getValue().floatValue();
					EntityUtil.moveEntityStrafe(this.yPortAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                }
            }
			case CPVPCC: {
			    if (MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player)) {
                    if (SpeedNew.mc.player.onGround) {
                        SpeedNew.mc.player.jump();
						EntityUtil.moveEntityStrafe(this.cpvpccSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
						break;
                    }
                    EntityUtil.moveEntityStrafe(Math.sqrt(Flight.mc.player.motionX * Flight.mc.player.motionX + Flight.mc.player.motionZ * Flight.mc.player.motionZ), (Entity)Flight.mc.player);
					break;
                } else {
                    SpeedNew.mc.player.motionX = 0.0;
                    SpeedNew.mc.player.motionZ = 0.0;
				}
            }
        }
    }

    @Override
    public void onTick() {
		switch (this.mode.getValue()) {
            case Custom: {
                if (MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player)) {
                    if (SpeedNew.mc.player.onGround) {
                        EntityUtil.moveEntityStrafe(this.onGroundSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
						if (this.autoJump.getValue().booleanValue()) {
                            SpeedNew.mc.player.motionY = this.jumpMotionY.getValue().floatValue();
					    	break;
						}
                    }
					if (SpeedNew.mc.player.motionY > 0) {
			            Experium.timerManager.setTimer(this.upTimerValue.getValue());
						EntityUtil.moveEntityStrafe(this.upAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                        break;
	                } else {
						if (this.downMode.getValue() == DownMode.Timer) {
		                    Experium.timerManager.setTimer(this.downTimerValue.getValue().floatValue());
					    } else if (this.downMode.getValue() == DownMode.Motion) {
							SpeedNew.mc.player.motionY =- this.downMotionValue.getValue().floatValue();
					    }
						EntityUtil.moveEntityStrafe(this.downAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                        break;
			        }
                }
                SpeedNew.mc.player.motionX = SpeedNew.mc.player.motionZ = 0.0;
                break;
            }
			case YPort: {
                if (!MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player) || SpeedNew.mc.player.collidedHorizontally) {
                    return;
                }
				if (this.yPortTimerSpeed.getValue().booleanValue()) {
					Experium.timerManager.setTimer(this.yPortTimerSpeedVal.getValue().floatValue());
				} else {
					Experium.timerManager.reset();
				}
				if (SpeedNew.mc.player.onGround) {
                    SpeedNew.mc.player.motionY = this.yPortJumpMotionY.getValue().floatValue();
                    EntityUtil.moveEntityStrafe(this.yPortGroundSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                } else {
                    SpeedNew.mc.player.motionY =- this.yPortFallSpeed.getValue().floatValue();
					EntityUtil.moveEntityStrafe(this.yPortAirSpeed.getValue().floatValue(), (Entity)SpeedNew.mc.player);
                }
            }
			case CPVPCC: {
			    if (MovementUtil.isMoving((EntityLivingBase)SpeedNew.mc.player)) {
                    if (SpeedNew.mc.player.onGround) {
                        SpeedNew.mc.player.jump();
						EntityUtil.moveEntityStrafe(this.cpvpccSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
						break;
                    }
                    EntityUtil.moveEntityStrafe(Math.sqrt(Flight.mc.player.motionX * Flight.mc.player.motionX + Flight.mc.player.motionZ * Flight.mc.player.motionZ), (Entity)Flight.mc.player);
					break;
                } else {
                    SpeedNew.mc.player.motionX = 0.0;
                    SpeedNew.mc.player.motionZ = 0.0;
				}
            }
        }
    }

    @Override
    public void onEnable() {
		if (this.mode.getValue() == SpeedNewModes.Custom) {
            if (this.resetXZ.getValue().booleanValue()) {
                SpeedNew.mc.player.motionX = SpeedNew.mc.player.motionZ = 0.0;
            }
            if (this.resetY.getValue().booleanValue()) {
                SpeedNew.mc.player.motionX = 0.0;
            }
		}
        super.onEnable();
    }
	
	@Override
    public void onDisable() {
        Experium.timerManager.reset();
		super.onDisable();
    }
	
	public static enum DownMode {
		Timer,
		Motion;
	}
	
	public static enum SpeedNewModes {
        Custom,
		CPVPCC,
		YPort;
    }
}
