package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.BlockCollisionBoundingBoxEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoWeb
        extends Module {
	public Setting<Mode> mode = this.register(new Setting<Mode>("NoWebType", Mode.Custom));
	public Setting<Double> horizonSpeed = register(new Setting<Double>("HorizonSpeed", 0.1, 0.01, 0.8, v -> this.mode.getValue() == Mode.Horizon));
    public Setting<Boolean> disableBB = register(new Setting<Boolean>("Add BB", true, v -> this.mode.getValue() == Mode.Custom));
    public Setting<Boolean> onGround = register(new Setting<Boolean>("On Ground", true, v -> this.mode.getValue() == Mode.Custom));
    public Setting<Double> bbOffset = register(new Setting<Double>("BB Offset", 0.0, -2.0, 2.0, v -> this.mode.getValue() == Mode.Custom));
    public Setting<Double> motionY = register(new Setting<Double>("MotionY", 1.0, 0.0, 20.0, v -> this.mode.getValue() == Mode.Custom));
    public Setting<Double> motionXZ = register(new Setting<Double>("MotionXZ", 0.84, -1.0, 5.0, v -> this.mode.getValue() == Mode.Custom));
	private boolean usedTimer = false;
	public float monkeys;

    public NoWeb() {
        super("NoWeb", "Prevents you from getting slowed by web", Module.Category.MOVEMENT, true, false, false);
    }

    @SubscribeEvent
    public void bbEvent(BlockCollisionBoundingBoxEvent event) {
        if (nullCheck()) return;
        if (NoWeb.mc.world.getBlockState(event.getPos()).getBlock() instanceof BlockWeb && this.mode.getValue() == Mode.Custom) {
            if (this.disableBB.getValue().booleanValue()) {
                event.setCanceled(true);
                event.setBoundingBox(Block.FULL_BLOCK_AABB.contract(0, this.bbOffset.getValue().doubleValue(), 0));
            }
        }
    }

    @Override
    public void onUpdate() {
		if (usedTimer) {
            Experium.timerManager.reset();
            usedTimer = false;
        }
		if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.Custom) {
            if (Keyboard.isKeyDown(NoWeb.mc.gameSettings.keyBindSneak.getKeyCode())) {
                NoWeb.mc.player.isInWeb = true;
                NoWeb.mc.player.motionY *= motionY.getValue();
            } else if (this.onGround.getValue().booleanValue()) {
                NoWeb.mc.player.onGround = false;
            }
            if (Keyboard.isKeyDown(NoWeb.mc.gameSettings.keyBindForward.keyCode) || Keyboard.isKeyDown(NoWeb.mc.gameSettings.keyBindBack.keyCode) || Keyboard.isKeyDown(NoWeb.mc.gameSettings.keyBindLeft.keyCode) || Keyboard.isKeyDown(NoWeb.mc.gameSettings.keyBindRight.keyCode)) {
                NoWeb.mc.player.isInWeb = false;
                NoWeb.mc.player.motionX *= motionXZ.getValue();
                NoWeb.mc.player.motionZ *= motionXZ.getValue();
            }
        }
		if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.OldAAC) {
            NoWeb.mc.player.jumpMovementFactor = 0.59f;
            if (!NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                NoWeb.mc.player.motionY = 0.0;;
            }
		}
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.LAAC) {
			if (NoWeb.mc.player.movementInput.moveStrafe != 0f) {
				this.monkeys = 1.0f;
			} else {
				this.monkeys = 1.21f;
			}
            NoWeb.mc.player.jumpMovementFactor = this.monkeys;
            if (!NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                NoWeb.mc.player.motionY = 0.0;
            }
            if (NoWeb.mc.player.onGround) {
                NoWeb.mc.player.jump();
            }
		}
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.AACv4) {
            Experium.timerManager.setTimer(0.99f);
            NoWeb.mc.player.jumpMovementFactor = 0.02958f;
            NoWeb.mc.player.motionY -= 0.00775;
            if (NoWeb.mc.player.onGround) {
                NoWeb.mc.player.motionY = 0.4050;
                Experium.timerManager.setTimer(1.35f);
            }
        }
		if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.Horizon) {
            if (NoWeb.mc.player.onGround) {
				EntityUtil.moveEntityStrafe(this.horizonSpeed.getValue().doubleValue(), NoWeb.mc.player);
            }
		}
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.Spartan) {
            EntityUtil.moveEntityStrafe(0.27f, NoWeb.mc.player);
            Experium.timerManager.setTimer(3.7f);
			if (!NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                NoWeb.mc.player.motionY = 0.0;
            }
            if (NoWeb.mc.player.ticksExisted % 2 == 0) {
                Experium.timerManager.setTimer(1.7f);
            }
            if (NoWeb.mc.player.ticksExisted % 40 == 0) {
                Experium.timerManager.setTimer(3.0f);
            }
            usedTimer = true;
        }
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.Matrix) {
            NoWeb.mc.player.jumpMovementFactor = 0.12425f;
            NoWeb.mc.player.motionY = -0.0125;
            if (NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
				NoWeb.mc.player.motionY = -0.1625;
			}
            if (NoWeb.mc.player.ticksExisted % 40 == 0) {
                Experium.timerManager.setTimer(3.0f);
                usedTimer = true;
            }
        }
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.AACv5) {
            NoWeb.mc.player.jumpMovementFactor = 0.42f;
            if (NoWeb.mc.player.onGround) {
                NoWeb.mc.player.jump();
            }
        }
        if (NoWeb.mc.player.isInWeb && !Experium.moduleManager.isModuleEnabled("Step") && this.mode.getValue() == Mode.Rewinside) {
            NoWeb.mc.player.jumpMovementFactor = 0.42f;
            if (NoWeb.mc.player.onGround) {
                NoWeb.mc.player.jump();
            }
        }
    }
	
	@Override
	public void onDisable() {
        Experium.timerManager.reset();
    }
	
	public static enum Mode {
		Custom,
		OldAAC,
		LAAC,
		AACv4,
		AACv5,
		Matrix,
		Horizon,
		Spartan,
		Rewinside;
	}
}
