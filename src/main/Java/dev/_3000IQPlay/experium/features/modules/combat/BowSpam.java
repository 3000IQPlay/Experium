package dev._3000IQPlay.experium.features.modules.combat;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

public class BowSpam
        extends Module {
    private final Setting<Integer> drawLength = this.register(new Setting<Integer>("Draw Length", 3, 3, 21));

    public BowSpam() {
        super("BowSpam", "", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && BowSpam.mc.player.isHandActive() && BowSpam.mc.player.getItemInUseMaxCount() >= this.drawLength.getValue()) {
            BowSpam.mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
            BowSpam.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItem(BowSpam.mc.player.getActiveHand()));
            BowSpam.mc.player.stopActiveHand();
        }
    }
}

