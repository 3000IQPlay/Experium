package dev._3000IQPlay.experium.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class CevBreaker
        extends Module {
    private List<BlockPos> placeList = new ArrayList<BlockPos>();
    private boolean placing = false;
    private boolean placedCrystal = false;
    private boolean breaking = false;
    private boolean broke = false;
    private EntityPlayer _target = null;
    private BlockPos b_crystal = null;
    private BlockPos breakPos = null;
    private int attempts = 0;
    private Setting<type> targetType = this.register(new Setting<type>("Target", type.NEAREST));
    private Setting<mode> breakMode = this.register(new Setting<mode>("Break Mode", mode.Vanilla));
    private Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    private Setting<Integer> startDelay = this.register(new Setting<Integer>("Start Delay", 1, 0, 10));
    private Setting<Integer> breakDelay = this.register(new Setting<Integer>("Break Delay", 1, 0, 10));
    private Setting<Integer> crystalDelay = this.register(new Setting<Integer>("Crystal Delay", 1, 0, 10));
    private Setting<Integer> hitDelay = this.register(new Setting<Integer>("Hit Delay", 3, 0, 10));
    private Setting<Integer> tinpoDelay = this.register(new Setting<Integer>("Tinpo Delay", 3, 0, 10));
    private int timer = 0;

    public CevBreaker() {
        super("CevBreaker", "Attack Ceil", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.init();
    }

    private void init() {
        this.placeList = new ArrayList<BlockPos>();
        this._target = null;
        this.b_crystal = null;
        this.placedCrystal = false;
        this.placing = false;
        this.breaking = false;
        this.broke = false;
        this.timer = 0;
        this.attempts = 0;
    }

    @Override
    public void onTick() {
        int pix = this.findItem(Items.DIAMOND_PICKAXE);
        int crystal = this.findItem(Items.END_CRYSTAL);
        int obby = this.findMaterials(Blocks.OBSIDIAN);
        if (pix == -1 || crystal == -1 || obby == -1) {
            this.disable();
            return;
        }
        if (this._target == null) {
            if (this.targetType.getValue() == type.NEAREST) {
                this._target = CevBreaker.mc.world.playerEntities.stream().filter(p -> p.getEntityId() != CevBreaker.mc.player.getEntityId()).min(Comparator.comparing(p -> Float.valueOf(p.getDistance((Entity) CevBreaker.mc.player)))).orElse(null);
            }
            if (this._target == null) {
                this.disable();
                return;
            }
        }
        if (this.placeList.size() == 0 && !this.placing) {
            this.searchSpace();
            if (this.placeList.size() == 0) {
                this.disable();
                return;
            }
        }
        if (!this.placedCrystal) {
            if (this.timer < this.startDelay.getValue()) {
                ++this.timer;
                return;
            }
            this.timer = 0;
            this.doPlace(obby, crystal);
        } else if (!this.breaking) {
            if (this.timer < this.breakDelay.getValue()) {
                ++this.timer;
                return;
            }
            this.timer = 0;
            if (this.breakMode.getValue() == mode.Vanilla) {
                CevBreaker.mc.player.inventory.currentItem = pix;
                CevBreaker.mc.playerController.updateController();
                CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                CevBreaker.mc.playerController.onPlayerDamageBlock(this.breakPos, EnumFacing.DOWN);
            } else {
                CevBreaker.mc.player.swingArm(EnumHand.MAIN_HAND);
                CevBreaker.mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
                CevBreaker.mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.breakPos, EnumFacing.DOWN));
            }
            this.breaking = true;
        } else if (this.breaking && !this.broke) {
            if (this.getBlock(this.breakPos) == Blocks.AIR) {
                this.broke = true;
            }
        } else if (this.broke) {
            if (this.timer < this.crystalDelay.getValue()) {
                ++this.timer;
                return;
            }
            this.timer = 0;
            Entity bcrystal = CevBreaker.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).min(Comparator.comparing(c -> Float.valueOf(c.getDistance((Entity) this._target)))).orElse(null);
            if (bcrystal == null) {
                if (this.attempts < this.hitDelay.getValue()) {
                    ++this.attempts;
                    return;
                }
                if (this.attempts < this.tinpoDelay.getValue()) {
                    ++this.attempts;
                    return;
                }
                this.placedCrystal = false;
                this.placeList.add(this.breakPos);
                this.breaking = false;
                this.broke = false;
                this.attempts = 0;
            } else {
                CevBreaker.mc.player.connection.sendPacket((Packet) new CPacketUseEntity(bcrystal));
                this.placedCrystal = false;
                this.placeList.add(this.breakPos);
                this.breaking = false;
                this.broke = false;
                this.attempts = 0;
            }
        }
    }

    private void doPlace(int obby, int crystal) {
        this.placing = true;
        if (this.placeList.size() != 0) {
            int oldslot = CevBreaker.mc.player.inventory.currentItem;
            CevBreaker.mc.player.inventory.currentItem = obby;
            CevBreaker.mc.playerController.updateController();
            BlockUtil.placeBlock(this.placeList.get(0), EnumHand.MAIN_HAND, this.rotate.getValue(), false, false);
            this.placeList.remove(0);
            CevBreaker.mc.player.inventory.currentItem = oldslot;
        } else if (!this.placedCrystal) {
            int oldslot = CevBreaker.mc.player.inventory.currentItem;
            if (crystal != 999) {
                CevBreaker.mc.player.inventory.currentItem = crystal;
            }
            CevBreaker.mc.playerController.updateController();
            CevBreaker.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.b_crystal, EnumFacing.UP, CevBreaker.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            CevBreaker.mc.player.inventory.currentItem = oldslot;
            this.placedCrystal = true;
        }
    }

    private void searchSpace() {
        BlockPos ppos = CevBreaker.mc.player.getPosition();
        BlockPos tpos = new BlockPos(this._target.posX, this._target.posY, this._target.posZ);
        this.placeList = new ArrayList<BlockPos>();
        BlockPos[] offset = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
        if (this.getBlock(new BlockPos(tpos.getX(), tpos.getY() + 3, tpos.getZ())) != Blocks.AIR || this.getBlock(new BlockPos(tpos.getX(), tpos.getY() + 4, tpos.getZ())) != Blocks.AIR) {
            return;
        }
        ArrayList<BlockPos> posList = new ArrayList<BlockPos>();
        for (int i = 0; i < offset.length; ++i) {
            BlockPos offsetPos = tpos.add((Vec3i) offset[i]);
            Block block = this.getBlock(offsetPos);
            if (block == Blocks.AIR || block instanceof BlockLiquid) continue;
            posList.add(offsetPos);
        }
        BlockPos base = posList.stream().max(Comparator.comparing(b -> this._target.getDistance((double) b.getX(), (double) b.getY(), (double) b.getZ()))).orElse(null);
        if (base == null) {
            return;
        }
        this.placeList.add(base);
        this.placeList.add(base.add(0, 1, 0));
        this.placeList.add(base.add(0, 2, 0));
        this.placeList.add(tpos.add(0, 2, 0));
        this.breakPos = tpos.add(0, 2, 0);
        this.b_crystal = tpos.add(0, 2, 0);
    }

    private int findMaterials(Block b) {
        for (int i = 0; i < 9; ++i) {
            if (!(CevBreaker.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) || ((ItemBlock) CevBreaker.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() != b)
                continue;
            return i;
        }
        return -1;
    }

    private int findItem(Item item) {
        if (item == Items.END_CRYSTAL && CevBreaker.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return 999;
        }
        for (int i = 0; i < 9; ++i) {
            if (CevBreaker.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    private Block getBlock(BlockPos b) {
        return CevBreaker.mc.world.getBlockState(b).getBlock();
    }

    public static enum mode {
        Vanilla,
        Packet;

    }

    public static enum type {
        NEAREST,
        LOOKING;

    }
}

