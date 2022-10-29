package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Flight
        extends Module {
    private static Flight INSTANCE = new Flight();
    public Setting<FlyMode> flyMode = this.register(new Setting<FlyMode>("FlyType", FlyMode.Motion));
	public Setting<Float> aacMotion = this.register(new Setting<Float>("AACMotion", 10.0f, 0.5f, 10.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> vanillaSpeed = this.register(new Setting<Float>("VanillaSpeed", 5.0f, 1.0f, 30.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> verticalSpeed = this.register(new Setting<Float>("VerticalSpeed", 10.0f, 1.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> keepAlive = this.register(new Setting<Boolean>("KeepAlive", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> noClip = this.register(new Setting<Boolean>("NoClip", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Boolean> spoof = this.register(new Setting<Boolean>("SpoofGround", false, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> jumpSpeed = this.register(new Setting<Float>("AutoAirJumpSpeed", 3.25f, 2.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.AutoAirJump));
	public Setting<Float> mHorizontalSpeed = this.register(new Setting<Float>("MHorizontalSpeed", 0.5f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> mVerticalSpeed = this.register(new Setting<Float>("MVerticalSpeed", 10.0f, 1.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.Vanilla));
	public Setting<Float> mTimerSpeed = this.register(new Setting<Float>("Timer", 1.0f, 0.1f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> jumpMotion = this.register(new Setting<Float>("JumpMotionY", 0.42f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.ManualAirJump));

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
	public void onUpdate() {
		if (this.flyMode.getValue() == FlyMode.Motion) {
		    Flight.mc.player.motionY = 0.0;
			if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
				Flight.mc.player.motionY += this.mVerticalSpeed.getValue().floatValue() / 10;
			}
            if (Flight.mc.gameSettings.keyBindSneak.isKeyDown()) {
				Flight.mc.player.motionY -= this.mVerticalSpeed.getValue().floatValue() / 10;
			}
		    Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
	        Experium.timerManager.setTimer(this.mTimerSpeed.getValue().floatValue());
	        EntityUtil.moveEntityStrafe(this.mHorizontalSpeed.getValue().floatValue() / 10, (Entity)Flight.mc.player);
		}
		if (this.flyMode.getValue() == FlyMode.ManualAirJump) {
		    Flight.mc.player.onGround = true;
		}
		if (this.flyMode.getValue() == FlyMode.AutoAirJump) {
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
			if (this.keepAlive.getValue().booleanValue()) {
                Flight.mc.player.connection.sendPacket(new CPacketKeepAlive());
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
			if (Flight.mc.gameSettings.keyBindJump.isKeyDown()) {
                Flight.mc.player.motionY += 0.15;
                Flight.mc.player.motionX *= 1.1;
                Flight.mc.player.motionZ *= 1.1;
			}
        }
		if (this.flyMode.getValue() == FlyMode.AACv3312) {
		    if (Flight.mc.player.posY < -70) {
                Flight.mc.player.motionY = this.aacMotion.getValue().floatValue();
            }
            Experium.timerManager.reset();
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                Experium.timerManager.setTimer(0.2f);
                Flight.mc.rightClickDelayTimer = 0;
            }
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
		AACv3312,
		AutoAirJump,
		ManualAirJump;
	}
}
