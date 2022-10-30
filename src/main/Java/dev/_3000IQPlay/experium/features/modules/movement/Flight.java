package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight
        extends Module {
    private static Flight INSTANCE = new Flight();
    public Setting<FlyMode> flyMode = this.register(new Setting<FlyMode>("FlyType", FlyMode.Motion));
	public Setting<Boolean> reDamage = this.register(new Setting<Boolean>("ReDamage", true, v -> this.flyMode.getValue() == FlyMode.VerusBoost));
	public Setting<Float> vbSpeed = this.register(new Setting<Float>("VerusBoostSpeed", 15.0f, 2.0f, 30.0f, v -> this.flyMode.getValue() == FlyMode.VerusBoost));
	public Setting<Float> vanillaSpeed = this.register(new Setting<Float>("VanillaSpeed", 5.0f, 2.0f, 30.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> verticalSpeed = this.register(new Setting<Float>("VerticalSpeed", 10.0f, 1.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> keepAlive = this.register(new Setting<Boolean>("KeepAlive", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> noClip = this.register(new Setting<Boolean>("NoClip", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> spoof = this.register(new Setting<Boolean>("SpoofGround", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> jumpSpeed = this.register(new Setting<Float>("AutoAirJumpSpeed", 3.25f, 2.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.AutoAirJump));
	public Setting<Float> mHorizontalSpeed = this.register(new Setting<Float>("HorizontalSpeed", 5.0f, 2.0f, 30.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> mTimerSpeed = this.register(new Setting<Float>("Timer", 1.0f, 0.1f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> jumpMotion = this.register(new Setting<Float>("JumpMotionY", 0.42f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.ManualAirJump));
	private boolean flyable;
	private int ticks = 0;
	private double y;

    public Flight() {
        super("Flight", "Makes you fly.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }
	
	public static Flight getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Flight();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
	
	@Override
	public void onEnable() {
		if (Flight.fullNullCheck()) {
			return;
		}
		this.ticks = 1;
    }
	
	@Override
	public void onUpdate() {
		if (this.flyMode.getValue() == FlyMode.Motion) {
		    Flight.mc.player.motionY = 0.0;
		    Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
	        Experium.timerManager.setTimer(this.mTimerSpeed.getValue().floatValue());
	        EntityUtil.moveEntityStrafe(this.mHorizontalSpeed.getValue().floatValue() / 10, (Entity)Flight.mc.player);
		}
		if (this.flyMode.getValue() == FlyMode.ManualAirJump) {
		    Flight.mc.player.onGround = true;
		}
		if (this.flyMode.getValue() == FlyMode.AutoAirJump) {
			Experium.timerManager.reset();
			if (Flight.mc.player.onGround) {
				Flight.mc.player.jump();
			}
			EntityUtil.moveEntityStrafe(this.jumpSpeed.getValue().floatValue() / 10, (Entity)Flight.mc.player);
			if (Flight.mc.player.fallDistance > 1) {
				Flight.mc.player.motionY = 0.5D;
				Flight.mc.player.fallDistance = 0F;
			}
		}
		if (this.flyMode.getValue() == FlyMode.Vanilla) {
			Experium.timerManager.reset();
			if (this.keepAlive.getValue().booleanValue()) {
                Flight.mc.player.connection.sendPacket((Packet)new CPacketKeepAlive());
            }
            if (this.noClip.getValue().booleanValue()) {
                Flight.mc.player.noClip = true;
            }
			Flight.mc.player.capabilities.isFlying = false;
            Flight.mc.player.motionX = Flight.mc.player.motionZ = Flight.mc.player.motionY = 0.0;
            if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
				Flight.mc.player.motionY += this.verticalSpeed.getValue().floatValue() / 10;
			}
            if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
				Flight.mc.player.motionY -= this.verticalSpeed.getValue().floatValue() / 10;
			}
            EntityUtil.moveEntityStrafe(this.vanillaSpeed.getValue().floatValue() / 10, (Entity)Flight.mc.player);
		}
		if (this.flyMode.getValue() == FlyMode.Jetpack) {
			Experium.timerManager.reset();
			if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
                Flight.mc.player.motionY += 0.15;
                Flight.mc.player.motionX *= 1.1;
                Flight.mc.player.motionZ *= 1.1;
			}
        }
		if (this.flyMode.getValue() == FlyMode.VerusBoost) {
			if (this.ticks == 1) {
            Flight.mc.player.connection.sendPacket((Packet)new CPacketEntityAction(Flight.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, true));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY + 3.42, Flight.mc.player.posZ, false));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, false));
            Flight.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Flight.mc.player.posX, Flight.mc.player.posY, Flight.mc.player.posZ, true));
            Experium.timerManager.setTimer(0.15f);
            Flight.mc.player.jump();
            Flight.mc.player.onGround = true;
            } else if (this.ticks == 2) {
                Experium.timerManager.reset();
            }
            if (Flight.mc.player.onGround) {
                Flight.mc.player.jump();
            }
            if (Flight.mc.player.fallDistance > 1) {
                Flight.mc.player.motionY = -((Flight.mc.player.posY) - Math.floor(Flight.mc.player.posY));
            }
            if (Flight.mc.player.motionY == 0.0) {
                Flight.mc.player.jump();
                Flight.mc.player.onGround = true;
                Flight.mc.player.fallDistance = 0f;
            }
            if (this.ticks < 25) {
                EntityUtil.moveEntityStrafe(this.vbSpeed.getValue().floatValue() / 10, (Entity)Flight.mc.player);
            } else {
                if (this.ticks == 25){
                    EntityUtil.moveEntityStrafe(0.48f, (Entity)Flight.mc.player);
                }
                if (this.reDamage.getValue().booleanValue()) {
                    this.ticks = 1;
                }
                EntityUtil.moveEntityStrafe(Math.sqrt(Flight.mc.player.motionX * Flight.mc.player.motionX + Flight.mc.player.motionZ * Flight.mc.player.motionZ), (Entity)Flight.mc.player);
            }
			this.ticks++;
        }
    }
	
	@SubscribeEvent
	public void onPacketSend(PacketEvent.Send event) {
		if (this.flyMode.getValue() == FlyMode.Vanilla) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (this.spoof.getValue().booleanValue()) {
				    Flight.mc.player.onGround = true;
			    }
            }
		}
    }
	
	@Override
	public void onDisable() {
		Experium.timerManager.reset();
		super.onDisable();
    }
	
	public static enum FlyMode {
		Motion,
		Vanilla,
		Jetpack,
		VerusBoost,
		AutoAirJump,
		ManualAirJump;
	}
}
