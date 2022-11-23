package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;

public class HighJump
        extends Module {
	public Setting<Mode> monkey = this.register(new Setting<Mode>("HightJumpType", Mode.Vanilla));
	public Setting<Double> height = this.register(new Setting<Double>("Height", 1.3, 1.0, 5.0));
	
	public HighJump() {
        super("HighJump", "Dont hit your head tho, it hurts", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
		if (this.monkey.getValue() == Mode.Vanilla) {
			if (!mc.player.onGround) {
                mc.player.motionY = mc.player.motionY * this.height.getValue().doubleValue();
			}
        }
        if (this.monkey.getValue() == Mode.AACv3) {
            if (!mc.player.onGround) {
	    		mc.player.motionY += 0.059f;
            }
		}
		if (this.monkey.getValue() == Mode.DAC) {
            if (!mc.player.onGround) {
				mc.player.motionY += 0.049999f;
			}
        }
    }
	
	public static enum Mode {
		Vanilla,
		AACv3,
		DAC,
	}
}