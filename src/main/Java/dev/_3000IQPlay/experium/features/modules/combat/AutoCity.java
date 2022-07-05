package dev._3000IQPlay.experium.features.modules.combat;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.BlockUtil;
import dev._3000IQPlay.experium.util.EntityUtil;
import dev._3000IQPlay.experium.util.PairUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCity
        extends Module {
    private static final BlockPos[] surroundOffset = new BlockPos[]{new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};
    public Setting<Boolean> raytrace = this.register(new Setting<Boolean>("Raytrace", false));
    public Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 1, 6));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Boolean> autodisable = this.register(new Setting<Boolean>("AutoDisable", true));
    public Setting<Integer> rotations = this.register(new Setting<Integer>("Spoofs", 1, 1, 20));

    public AutoCity() {
        super("AutoCity", "For lazy apes.", Module.Category.COMBAT, true, false, false);
    }

    public static ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> GetPlayersReadyToBeCitied() {
        ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> arrayList = new ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>>();
        for (EntityPlayer entity : Objects.requireNonNull(EntityUtil.getNearbyPlayers(6.0)).stream().filter(entityPlayer -> !Experium.friendManager.isFriend((EntityPlayer) entityPlayer)).collect(Collectors.toList())) {
            ArrayList<BlockPos> arrayList2 = new ArrayList<BlockPos>();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos = EntityUtil.GetPositionVectorBlockPos((Entity) entity, surroundOffset[i]);
                if (AutoCity.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) continue;
                boolean bl = false;
                switch (i) {
                    case 0: {
                        bl = BlockUtil.canPlaceCrystal(blockPos.north(2), true, false);
                        break;
                    }
                    case 1: {
                        bl = BlockUtil.canPlaceCrystal(blockPos.east(2), true, false);
                        break;
                    }
                    case 2: {
                        bl = BlockUtil.canPlaceCrystal(blockPos.south(2), true, false);
                        break;
                    }
                    case 3: {
                        bl = BlockUtil.canPlaceCrystal(blockPos.west(2), true, false);
                    }
                }
                if (!bl) continue;
                arrayList2.add(blockPos);
            }
            if (arrayList2.isEmpty()) continue;
            arrayList.add(new PairUtil(entity, arrayList2));
        }
        return arrayList;
    }

    @Override
    public void onEnable() {
        ArrayList<PairUtil<EntityPlayer, ArrayList<BlockPos>>> arrayList = AutoCity.GetPlayersReadyToBeCitied();
        if (arrayList.isEmpty()) {
            Command.sendMessage("There is no one to city!");
            this.toggle();
            return;
        }
        EntityPlayer entityPlayer = null;
        BlockPos blockPos = null;
        double d = 50.0;
        for (PairUtil<EntityPlayer, ArrayList<BlockPos>> pairUtil : arrayList) {
            for (BlockPos blockPos2 : pairUtil.getSecond()) {
                if (blockPos == null) {
                    entityPlayer = pairUtil.getFirst();
                    blockPos = blockPos2;
                    continue;
                }
                double d2 = blockPos2.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (!(d2 < d)) continue;
                d = d2;
                blockPos = blockPos2;
                entityPlayer = pairUtil.getFirst();
            }
        }
        if (blockPos == null || entityPlayer == null) {
            Command.sendMessage("Couldn't find any blocks to mine!");
            this.toggle();
            return;
        }
        BlockUtil.SetCurrentBlock(blockPos);
        Command.sendMessage("Attempting to mine a block by your target: " + entityPlayer.getName());
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent updateWalkingPlayerEvent) {
        boolean bl;
        boolean bl2 = bl = AutoCity.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;
        if (!bl) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemStack = AutoCity.mc.player.inventory.getStackInSlot(i);
                if (itemStack.isEmpty() || itemStack.getItem() != Items.DIAMOND_PICKAXE) continue;
                bl = true;
                AutoCity.mc.player.inventory.currentItem = i;
                AutoCity.mc.playerController.updateController();
                break;
            }
        }
        if (!bl) {
            Command.sendMessage("No pickaxe!");
            this.toggle();
            return;
        }
        BlockPos blockPos = BlockUtil.GetCurrBlock();
        if (blockPos == null) {
            if (this.autodisable.getValue().booleanValue()) {
                Command.sendMessage("Done!");
                this.toggle();
            }
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            Experium.rotationManager.updateRotations();
            Experium.rotationManager.lookAtPos(blockPos);
            updateWalkingPlayerEvent.setCanceled(true);
        }
        BlockUtil.Update(this.range.getValue().intValue(), this.raytrace.getValue());
    }
}