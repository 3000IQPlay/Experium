package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;

public class Flight
        extends Module {
    private static Flight INSTANCE = new Flight();
    public Setting<FlyMode> flyMode = this.register(new Setting<FlyMode>("FlyType", FlyMode.Motion));
	public Setting<Float> jumpSpeed = this.register(new Setting<Float>("AutoAirJumpSpeed", 3.25f, 2.0f, 10.0f, v -> this.flyMode.getValue() == FlyMode.AutoAirJump));
	public Setting<Float> hrizontalSpeed = this.register(new Setting<Float>("HorizontalSpeed", 0.5f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> timerSpeed = this.register(new Setting<Float>("Timer", 1.0f, 0.1f, 5.0f, v -> this.flyMode.getValue() == FlyMode.Motion));
	public Setting<Float> jumpMotion = this.register(new Setting<Float>("JumpMotionY", 0.42f, 0.1f, 3.0f, v -> this.flyMode.getValue() == FlyMode.ManualAirJump));
	public Timer timer = new Timer();

    public Flight() {
        super("Flight", "Makes you fly.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }
	
	@Override
	public void onUpdate() {
		if (this.flyMode.getValue() == FlyMode.Motion) {
		    Flight.mc.player.motionY = 0.0;
		    Flight.mc.player.setVelocity(0.0, 0.0, 0.0);
	        Experium.timerManager.setTimer(this.timerSpeed.getValue().floatValue());
	        EntityUtil.moveEntityStrafe(this.hrizontalSpeed.getValue().floatValue(), (Entity)Flight.mc.player);
		}
		if (this.flyMode.getValue() == FlyMode.ManualAirJump) {
		    Flight.mc.player.onGround = true;
		}
		if (this.flyMode.getValue() == FlyMode.Vanilla) {
    		Flight.mc.player.capabilities.isFlying = true;
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
    }
	
	@Override
	public void onDisable() {
		Experium.timerManager.reset();
		super.onDisable();
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
	
	public static enum FlyMode {
		Motion,
		Vanilla,
		AutoAirJump,
		ManualAirJump;
	}
}
