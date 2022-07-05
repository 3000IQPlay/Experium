package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.mixin.mixins.accessors.ICPacketPlayer;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiHunger
        extends Module {
    public Setting<Boolean> sprint = this.register(new Setting<Boolean>("Sprint", true));
    public Setting<Boolean> noGround = this.register(new Setting<Boolean>("Ground", true));
    private boolean isOnGround = false;

    public AntiHunger() {
        super("AntiHunger", "Prevents hunger loss", Module.Category.PLAYER, true, false, false);
    }

    public void onEnable() {
        if (sprint.getValue() && mc.player != null) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    public void onDisable() {
        if (sprint.getValue() && mc.player != null && mc.player.isSprinting()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketEntityAction) {
            CPacketEntityAction action = (CPacketEntityAction) event.getPacket();
            if (sprint.getValue() && (action.getAction() == CPacketEntityAction.Action.START_SPRINTING || action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
                event.setCanceled(true);
            }
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer player = (CPacketPlayer) event.getPacket();
            boolean ground = mc.player.onGround;
            if (noGround.getValue() && isOnGround && ground && player.getY(0.0) == (!((ICPacketPlayer) player).isMoving() ? 0.0 : mc.player.posY)) {
                ((ICPacketPlayer) player).setOnGround(false);
            }
            isOnGround = ground;
        }
    }
}