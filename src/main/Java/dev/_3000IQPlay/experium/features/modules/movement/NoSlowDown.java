package dev._3000IQPlay.experium.features.modules.movement;

import dev._3000IQPlay.experium.event.events.KeyEvent;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.event.events.PlayerUpdateMoveEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowDown
        extends Module {
	private static final KeyBinding[] keys = new KeyBinding[]{NoSlowDown.mc.gameSettings.keyBindForward, NoSlowDown.mc.gameSettings.keyBindBack, NoSlowDown.mc.gameSettings.keyBindLeft, NoSlowDown.mc.gameSettings.keyBindRight, NoSlowDown.mc.gameSettings.keyBindJump, NoSlowDown.mc.gameSettings.keyBindSprint};
	public Setting<ItemMode> itemMode = this.register(new Setting<ItemMode>("ItemMode", ItemMode.Normal));
	public Setting<Boolean> items = this.register(new Setting<Boolean>("Items", true));
	public Setting<Boolean> guiMove = this.register(new Setting<Boolean>("GuiMove", true));
    public Setting<Boolean> sneak = this.register(new Setting<Boolean>("Sneak", false));
	boolean sneakingFlag;
	
	public NoSlowDown() {
        super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
    }
	
    @SubscribeEvent
    public void onUpdate(PlayerUpdateMoveEvent event) {
		if (this.guiMove.getValue().booleanValue()) {
            if (NoSlowDown.mc.currentScreen instanceof GuiOptions || NoSlowDown.mc.currentScreen instanceof GuiVideoSettings || NoSlowDown.mc.currentScreen instanceof GuiScreenOptionsSounds || NoSlowDown.mc.currentScreen instanceof GuiContainer || NoSlowDown.mc.currentScreen instanceof GuiIngameMenu) {
                for (KeyBinding bind : keys) {
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                }
            } else if (NoSlowDown.mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
        if (this.itemMode.getValue() == ItemMode._2B2TSneak && !mc.player.isSneaking() && !mc.player.isRiding()) {
            if (mc.player.isHandActive()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneakingFlag = true;
            }
            else if (sneakingFlag) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneakingFlag = false;
            }
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if ((this.items.getValue().booleanValue() && mc.player.isHandActive() && !mc.player.isRiding()) || (this.sneak.getValue().booleanValue() && mc.player.isSneaking())) {
            event.getMovementInput().moveForward /= 0.2f;
            event.getMovementInput().moveStrafe /= 0.2f;
        }
    }
	
	@SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if (this.guiMove.getValue().booleanValue() && event.getStage() == 0 && !(NoSlowDown.mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.items.getValue().booleanValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            switch (this.itemMode.getValue()) {
                case NCPStrict: {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
                    break;
                }
                case _2B2TBypass: {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    break;
                }
            }
        }
    }


    public enum ItemMode {
        Normal,
        NCPStrict,
        _2B2TSneak,
        _2B2TBypass;
    }
}
