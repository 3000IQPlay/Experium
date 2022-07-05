package dev._3000IQPlay.experium.features.modules.misc;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.features.setting.Bind;
import dev._3000IQPlay.experium.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyChorus
        extends Module {
    private boolean clicked = false;
    private Setting<Bind> keyBind = this.register(new Setting<Bind>("Key", new Bind(-1)));

    public KeyChorus() {
        super("KeyChorus", "When the key gets pressed you will eat chorus", Module.Category.MISC, false, false, false);
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
    }

    @Override
    public void onTick() {
        if (Keyboard.getEventKey() == this.keyBind.getValue().getKey()) {
            if (!this.clicked) {
                this.eatChorus();
            }
        } else {
            this.clicked = false;
        }
    }

    private void eatChorus() {
        boolean offhand;
        int chorusSlot = InventoryUtil.findHotbarBlock(ItemChorusFruit.class);
        boolean bl = offhand = mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT;
        if (chorusSlot != -1 || offhand) {
            int oldslot = mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(chorusSlot, true);
            }
            mc.player.connection.sendPacket(new CPacketPlayer());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.playerController.processRightClick(mc.player, mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
            this.clicked = true;
        }
    }

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem().equals(Items.CHORUS_FRUIT)) {
            event.getItemStack().getItem().onItemUseFinish(event.getItemStack(), event.getWorld(), event.getEntityPlayer());
        }
    }
}