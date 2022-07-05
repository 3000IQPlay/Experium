package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.event.events.PacketEvent;
import dev._3000IQPlay.experium.features.modules.Module;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiChunkBan
        extends Module {
    public AntiChunkBan() {
        super("AntiChunkBan", "Prevents you from getting chunk banned (can desync you)", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        Object p = event.getPacket();
        if (p instanceof SPacketBlockChange) {
            event.setCanceled(true);
        } else if (p instanceof SPacketUpdateTileEntity) {
            event.setCanceled(true);
        } else if (p instanceof SPacketMultiBlockChange) {
            event.setCanceled(true);
        } else if (p instanceof SPacketChunkData) {
            SPacketChunkData data = (SPacketChunkData)p;
            Chunk chunk = AntiChunkBan.mc.world.getChunk(data.getChunkX(), data.getChunkZ());
            if (chunk.isLoaded()) {
                event.setCanceled(true);
            }
        } else if (p instanceof SPacketBlockAction) {
            event.setCanceled(true);
        }
    }
}