package dev._3000IQPlay.experium.features.modules.player;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemMinecart;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class FastUse
        extends Module {
    private Setting<Boolean> all = this.register(new Setting<Boolean>("All", false));
    private Setting<Boolean> obby = this.register(new Setting<Object>("Obsidian", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> crystals = this.register(new Setting<Object>("Crystals", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> exp = this.register(new Setting<Object>("Experience", Boolean.valueOf(false), v -> this.all.getValue() == false));
	private Setting<Boolean> minecart = this.register(new Setting<Object>("Minecarts", Boolean.valueOf(false), v -> this.all.getValue() == false));
    private Setting<Boolean> packetCrystal = this.register(new Setting<Boolean>("PacketCrystal", false));
	private Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    private BlockPos mousePos = null;

    public FastUse() {
        super("FastUse", "Allows you to use items faster", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
		if (strict.getValue() && mc.player.ticksExisted % 2 == 0) return;
        if (FastUse.fullNullCheck()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.exp.getValue().booleanValue()) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(BlockObsidian.class) && this.obby.getValue().booleanValue()) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
		if (InventoryUtil.holdingItem(ItemMinecart.class) && this.minecart.getValue().booleanValue()) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (this.all.getValue().booleanValue()) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (this.crystals.getValue().booleanValue() || this.all.getValue().booleanValue())) {
            FastUse.mc.rightClickDelayTimer = 0;
        }
        if (this.packetCrystal.getValue().booleanValue() && FastUse.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            boolean offhand;
            boolean bl = offhand = FastUse.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            if (offhand || FastUse.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                RayTraceResult result = FastUse.mc.objectMouseOver;
                if (result == null) {
                    return;
                }
                switch (result.typeOfHit) {
                    case MISS: {
                        this.mousePos = null;
                        break;
                    }
                    case BLOCK: {
                        this.mousePos = FastUse.mc.objectMouseOver.getBlockPos();
                        break;
                    }
                    case ENTITY: {
                        Entity entity;
                        if (this.mousePos == null || (entity = result.entityHit) == null || !this.mousePos.equals(new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ))) break;
                        FastUse.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                    }
                }
            }
        }
    }
}
