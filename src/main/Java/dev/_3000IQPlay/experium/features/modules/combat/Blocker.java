package dev._3000IQPlay.experium.features.modules.combat;

import dev._3000IQPlay.experium.util.BlockUtil;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;

public class Blocker
        extends Module {
	private Setting<Float> range = this.register(new Setting<Float>("Range", 6.1f, 1.0f, 10.0f));
    private Setting<Boolean> piston = this.register(new Setting<Boolean>("Piston", true));
    private Setting<Boolean> cev = this.register(new Setting<Boolean>("CevBreaker", true));
    private Setting<Boolean> packetPlace = this.register(new Setting<Boolean>("PacketPlace", true));
    private BlockPos b_piston = null;
    private BlockPos b_cev = null;

    public Blocker() {
        super("Blocker", "Anti-PistonFag.class", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (Blocker.mc.player == null) {
            return;
        }
        int ob = this.findMaterials(Blocks.OBSIDIAN);
        if (ob == -1) {
            return;
        }
        BlockPos p_pos = new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY, Blocker.mc.player.posZ);
        if (this.piston.getValue().booleanValue()) {
            BlockPos[] offset = new BlockPos[]{new BlockPos(2, 1, 0), new BlockPos(-2, 1, 0), new BlockPos(0, 1, 2), new BlockPos(0, 1, -2)};
            for (int y = 0; y < 4; ++y) {
                for (int i = 0; i < offset.length; ++i) {
                    BlockPos pre_piston = p_pos.add((Vec3i)offset[i].add(0, y, 0));
                    if (this.getBlock(pre_piston).getBlock() != Blocks.PISTON && this.getBlock(pre_piston).getBlock() != Blocks.STICKY_PISTON) continue;
                    this.b_piston = pre_piston;
                }
            }
            if (this.b_piston != null) {
                if (this.getBlock(this.b_piston).getBlock() == Blocks.AIR) {
                    if (Blocker.mc.player.getDistance((double)this.b_piston.getX(), (double)this.b_piston.getY(), (double)this.b_piston.getZ()) > (double)this.range.getValue().floatValue()) {
                        return;
                    }
                    int oldslot = Blocker.mc.player.inventory.currentItem;
                    Blocker.mc.player.inventory.currentItem = ob;
                    Blocker.mc.playerController.updateController();
                    BlockUtil.placeBlock(this.b_piston, EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                    Blocker.mc.player.inventory.currentItem = oldslot;
                    Blocker.mc.playerController.updateController();
                }
                if (this.getBlock(this.b_piston).getBlock() == Blocks.OBSIDIAN || Blocker.mc.player.getDistance((double)this.b_piston.getX(), (double)this.b_piston.getY(), (double)this.b_piston.getZ()) > (double)this.range.getValue().floatValue()) {
                    this.b_piston = null;
                }
            }
        }
        if (this.cev.getValue().booleanValue()) {
            BlockPos p_player = new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY, Blocker.mc.player.posZ);
            Entity crystal = Blocker.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).filter(c -> c.posY > (double)p_player.getY()).filter(c -> Blocker.mc.player.getDistance(c.posX, Blocker.mc.player.posY, c.posZ) < 1.0).min(Comparator.comparing(c -> Blocker.mc.player.getDistanceSq(c.posX, c.posY, c.posZ))).orElse(null);
            if (this.getBlock(new BlockPos(p_player.getX(), p_player.getY() + 2, p_player.getZ())).getBlock() == Blocks.OBSIDIAN && crystal != null) {
                this.b_cev = new BlockPos(crystal.posX, crystal.posY, crystal.posZ);
            }
            if (this.b_cev != null && this.getBlock(this.b_cev).getBlock() == Blocks.AIR) {
                if (Blocker.mc.player.getDistance((double)this.b_cev.getX(), (double)this.b_cev.getY(), (double)this.b_cev.getZ()) > (double)this.range.getValue().floatValue()) {
                    return;
                }
                if (crystal == null) {
                    BlockPos blockPos = new BlockPos(Blocker.mc.player.posX, (double)this.b_cev.getY(), Blocker.mc.player.posZ);
                    if (blockPos.getDistance(this.b_cev.getX(), this.b_cev.getY(), this.b_cev.getZ()) < 1.0) {
                        int oldslot = Blocker.mc.player.inventory.currentItem;
                        Blocker.mc.player.inventory.currentItem = ob;
                        Blocker.mc.playerController.updateController();
                        BlockUtil.placeBlock(this.b_cev.add(0, -1, 0), EnumHand.MAIN_HAND, true, false, false);
                        BlockUtil.placeBlock(this.b_cev, EnumHand.MAIN_HAND, true, false, false);
                        Blocker.mc.player.inventory.currentItem = oldslot;
                        Blocker.mc.playerController.updateController();
                        this.b_cev = null;
                    }
                }
            }
        }
    }

    private int findMaterials(Block b) {
        for (int i = 0; i < 9; ++i) {
            if (!(Blocker.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) || ((ItemBlock)Blocker.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() != b) continue;
            return i;
        }
        return -1;
    }

    public BlockPos getPistonPos() {
        return this.b_piston;
    }

    private IBlockState getBlock(BlockPos o) {
        return Blocker.mc.world.getBlockState(o);
    }
}