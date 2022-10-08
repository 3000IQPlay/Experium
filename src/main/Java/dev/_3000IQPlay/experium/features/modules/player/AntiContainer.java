package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.event.events.PacketEvent;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiContainer
        extends Module {
	public Setting<Boolean> chest = this.register(new Setting<Boolean>("Chest", true));
	public Setting<Boolean> enderChest = this.register(new Setting<Boolean>("Ender Chest", true));
	public Setting<Boolean> trappedChest = this.register(new Setting<Boolean>("Trapped Chest", true));
	public Setting<Boolean> hopper = this.register(new Setting<Boolean>("Hopper", true));
	public Setting<Boolean> dispenser = this.register(new Setting<Boolean>("Dispenser", true));
	public Setting<Boolean> furnace = this.register(new Setting<Boolean>("Furnace", true));
	public Setting<Boolean> beacon = this.register(new Setting<Boolean>("Beacon", true));
	public Setting<Boolean> craftingTable = this.register(new Setting<Boolean>("Crafting Table", true));
	public Setting<Boolean> anvil = this.register(new Setting<Boolean>("Anvil", true));
	public Setting<Boolean> enchantingTable = this.register(new Setting<Boolean>("Enchanting Table", true));
	public Setting<Boolean> brewingStand = this.register(new Setting<Boolean>("Brewing Stand", true));
	public Setting<Boolean> shulkerBox = this.register(new Setting<Boolean>("Shulker Box", true));
	
	public AntiContainer() {
        super("AntiContainer", "Avoiding opening containers", Module.Category.PLAYER, true, false, false);
    }

	@SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            BlockPos pos = ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos();
            if (check(pos)) {
			    event.setCanceled(true);
			}
        }
    }

    public boolean check(BlockPos pos) {
        return ((Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.CHEST && chest.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST && this.enderChest.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.TRAPPED_CHEST && this.trappedChest.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.HOPPER && this.hopper.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.DISPENSER && this.dispenser.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.FURNACE && this.furnace.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.BEACON && this.beacon.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE && this.craftingTable.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ANVIL && this.anvil.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.ENCHANTING_TABLE && this.enchantingTable.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() == Blocks.BREWING_STAND && this.brewingStand.getValue())
                || (Minecraft.getMinecraft().world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) && this.shulkerBox.getValue());
    }
}