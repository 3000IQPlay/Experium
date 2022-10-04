package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiBan
extends Module {
        public Setting<Boolean> Achunkban = this.register(new Setting<Boolean>("AntiChunkBan", false));
        public Setting<Boolean> AbookBan = this.register(new Setting<Boolean>("AntiBookBan", false));

    public AntiBan() {
        super("AntiBan", "Prevents You From Getting Banned Using Gay Exploits!", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive receive) {
        if (this.Achunkban.getValue().booleanValue()) {
            Object t = receive.getPacket();
            if (t instanceof SPacketBlockChange) {
                receive.setCanceled(true);
            } else if (t instanceof SPacketUpdateTileEntity) {
                receive.setCanceled(true);
            } else if (t instanceof SPacketMultiBlockChange) {
                receive.setCanceled(true);
            } else if (t instanceof SPacketChunkData) {
                SPacketChunkData sPacketChunkData = (SPacketChunkData)t;
                Chunk chunk = AntiBan.mc.world.getChunk(sPacketChunkData.getChunkX(), sPacketChunkData.getChunkZ());
                if (chunk.isLoaded()) {
                    receive.setCanceled(true);
                }
            } else if (t instanceof SPacketBlockAction) {
                receive.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLeavingDeathEvent(PacketEvent.Receive receive) {
        if (this.AbookBan.getValue().booleanValue()) {
            if (!(receive.getPacket() instanceof CPacketClickWindow)) {
                return;
            }
            CPacketClickWindow cPacketClickWindow = (CPacketClickWindow)receive.getPacket();
            if (!(cPacketClickWindow.getClickedItem().getItem() instanceof ItemWrittenBook)) {
                return;
            }
            receive.isCanceled();
            AntiBan.mc.player.openContainer.slotClick(cPacketClickWindow.getSlotId(), cPacketClickWindow.getUsedButton(), cPacketClickWindow.getClickType(), (EntityPlayer)AntiBan.mc.player);
        }
    }
}