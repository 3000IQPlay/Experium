package dev._3000IQPlay.experium.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev._3000IQPlay.experium.Experium;
import dev._3000IQPlay.experium.features.command.Command;
import dev._3000IQPlay.experium.features.modules.Module;
import dev._3000IQPlay.experium.features.modules.combat.AutoCrystal;
import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.util.BlockUtil;
import dev._3000IQPlay.experium.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class PistonAura
        extends Module {
    public EntityPlayer target = null;
    private Setting<redstone> redstoneType = this.register(new Setting<redstone>("Redstone", redstone.Torch, " redstone type"));
    private Setting<Integer> start_delay = this.register(new Setting<Integer>("Start Delay", 1, 0, 10));
    private Setting<Integer> place_delay = this.register(new Setting<Integer>("Place Delay", 1, 0, 10));
    private Setting<Integer> crystal_delay = this.register(new Setting<Integer>("Crystal Delay", 1, 0, 10));
    private Setting<Integer> break_delay = this.register(new Setting<Integer>("Break Delay", 1, 0, 10));
    private Setting<Integer> break_attempts = this.register(new Setting<Integer>("Break Attempts", 2, 1, 10));
    private Setting<types> target_type = this.register(new Setting<types>("Target", types.Looking));
    private Setting<Integer> MaxY = this.register(new Setting<Integer>("MaxY", 2, 1, 5));
    private Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(5.2f), Float.valueOf(1.0f), Float.valueOf(15.0f)));
    private Setting<mode> trap = this.register(new Setting<mode>("Trap Mode", mode.Smart));
    private Setting<Boolean> packetPlace = this.register(new Setting<Boolean>("PacketPlace", false));
    private Setting<arm> swingArm = this.register(new Setting<arm>("Swing Arm", arm.MainHand));
    private Setting<Boolean> antiweakness = this.register(new Setting<Boolean>("AntiWeakness", false));
    private Setting<Boolean> toggle = this.register(new Setting<Boolean>("Toggle", true));
    private boolean r_redstone = false;
    private int b_stage = 0;
    private BlockPos b_crystal = null;
    private BlockPos b_piston = null;
    private BlockPos b_redStone = null;
    private boolean p_crystal = false;
    private boolean p_piston = false;
    private boolean p_redstone = false;
    private boolean s_crystal = false;
    private boolean u_crystal = false;
    private int attempts = 0;
    private int crystalId = 0;
    private int trapprogress = 0;
    private int timer = 0;
    private boolean autoGG = false;
    private int debug_stage = -1;
    private List<BlockPos> c_crystal = null;
    private List<BlockPos> c_piston = null;
    private List<BlockPos> c_redStone = null;
    private boolean isTorch = false;

    public PistonAura() {
        super("PistonAura", "Sleeping... :3", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.Init();
        this.autoGG = false;
    }

    @Override
    public void onTick() {
        try {
            boolean doTrap;
            int oldslot = PistonAura.mc.player.inventory.currentItem;
            int pickaxe = this.findItem(Items.DIAMOND_PICKAXE);
            int crystal = this.findItem(Items.END_CRYSTAL);
            int piston = this.findMaterials((Block) Blocks.PISTON);
            if (piston == -1) {
                piston = this.findMaterials((Block) Blocks.STICKY_PISTON);
            }
            int redstone2 = this.findMaterials(Blocks.REDSTONE_TORCH);
            this.isTorch = true;
            if (this.redstoneType.getValue() == redstone.Block || this.redstoneType.getValue() == redstone.Both && redstone2 == -1) {
                redstone2 = this.findMaterials(Blocks.REDSTONE_BLOCK);
                this.isTorch = false;
            }
            int obsidian = this.findMaterials(Blocks.OBSIDIAN);
            int sword = this.findItem(Items.DIAMOND_SWORD);
            if (this.antiweakness.getValue().booleanValue() && sword == -1) {
                sword = pickaxe;
            }
            if (pickaxe == -1 || crystal == -1 || piston == -1 || redstone2 == -1 || obsidian == -1) {
                Command.sendMessage("Missing Materials! disabling...");
                this.disable();
                return;
            }
            this.debug_stage = 0;
            if (this.target == null) {
                if (this.target_type.getValue() == types.Nearest) {
                    this.target = PistonAura.mc.world.playerEntities.stream().filter(p -> p.entityId != PistonAura.mc.player.entityId).min(Comparator.comparing(p -> Float.valueOf(PistonAura.mc.player.getDistance((Entity) p)))).orElse(null);
                }
                if (this.target_type.getValue() == types.Looking) {
                    // empty if block
                }
                if (this.target == null) {
                    this.disable();
                    return;
                }
            }
            this.debug_stage = 1;
            if (this.b_crystal == null || this.b_piston == null || this.b_redStone == null) {
                this.searchSpace();
                if (this.b_crystal == null || this.b_piston == null || this.b_redStone == null) {
                    if (this.toggle.getValue().booleanValue()) {
                        Command.sendMessage("Not found space! disabling...");
                        this.disable();
                    }
                    return;
                }
            }
            this.debug_stage = 2;
            if (this.getRange(this.b_crystal) > this.range.getValue().floatValue() || this.getRange(this.b_piston) > this.range.getValue().floatValue() || this.getRange(this.b_redStone) > this.range.getValue().floatValue()) {
                Command.sendMessage("Out of range! disabling...");
                if (this.toggle.getValue().booleanValue()) {
                    this.disable();
                }
                return;
            }
            this.debug_stage = 3;
            boolean bl = doTrap = this.trap.getValue() == mode.Force || this.trap.getValue() == mode.Smart && Experium.holeManager.isSafe(PistonAura.mc.player.getPosition()) && this.b_piston.getY() == PistonAura.mc.player.getPosition().getY() + 1;
            if (doTrap && PistonAura.mc.world.getBlockState(new BlockPos(this.target.posX, this.target.posY + 2.0, this.target.posZ)).getBlock() == Blocks.AIR) {
                if (this.timer < this.place_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                PistonAura.mc.player.inventory.currentItem = obsidian;
                PistonAura.mc.playerController.updateController();
                BlockPos first = new BlockPos((Math.floor(this.target.posX) - (double) this.b_crystal.getX()) * 1.0 + this.target.posX, (double) this.b_piston.getY(), (Math.floor(this.target.posZ) - (double) this.b_crystal.getZ()) * 1.0 + this.target.posZ);
                if (this.trapprogress == 0 || this.trapprogress == 1) {
                    BlockPos pos = first;
                    if (this.trapprogress == 1) {
                        pos = new BlockPos(first.getX(), first.getY() + 1, first.getZ());
                    }
                    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                } else {
                    BlockUtil.placeBlock(new BlockPos(this.target.posX, this.target.posY + 2.0, this.target.posZ), EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                }
                ++this.trapprogress;
                return;
            }
            this.debug_stage = 4;
            this.debug_stage = 5;
            if (this.getBlock(this.b_piston.add(0, -1, 0)).getBlock() == Blocks.AIR) {
                PistonAura.mc.player.inventory.currentItem = obsidian;
                PistonAura.mc.playerController.updateController();
                if (this.timer < this.place_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                BlockUtil.placeBlock(this.b_piston.add(0, -1, 0), EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                return;
            }
            if (this.getBlock(this.b_redStone.add(0, -1, 0)).getBlock() == Blocks.AIR && this.isTorch) {
                PistonAura.mc.player.inventory.currentItem = obsidian;
                PistonAura.mc.playerController.updateController();
                if (this.timer < this.place_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                BlockUtil.placeBlock(this.b_redStone.add(0, -1, 0), EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                return;
            }
            this.debug_stage = 6;
            if (this.r_redstone) {
                if (this.getBlock(this.b_redStone).getBlock() == Blocks.AIR) {
                    this.r_redstone = false;
                    this.b_stage = 0;
                    this.p_crystal = false;
                    this.p_redstone = false;
                    return;
                }
                PistonAura.mc.player.inventory.currentItem = pickaxe;
                PistonAura.mc.playerController.updateController();
                if (this.b_stage == 0) {
                    PistonAura.mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.b_redStone, EnumFacing.DOWN));
                    this.b_stage = 1;
                } else if (this.b_stage == 1) {
                    PistonAura.mc.playerController.onPlayerDamageBlock(this.b_redStone, EnumFacing.DOWN);
                }
                return;
            }
            this.debug_stage = 7;
            if (!this.p_piston) {
                if (this.timer < this.place_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                PistonAura.mc.player.inventory.currentItem = piston;
                PistonAura.mc.playerController.updateController();
                float[] angle = MathUtil.calcAngle(new Vec3d((Vec3i) this.b_piston), new Vec3d((Vec3i) this.b_crystal));
                PistonAura.mc.player.connection.sendPacket((Packet) new CPacketPlayer.Rotation(angle[0] + 180.0f, angle[1], true));
                BlockUtil.placeBlock(this.b_piston, EnumHand.MAIN_HAND, false, this.packetPlace.getValue(), false);
                this.p_piston = true;
            }
            this.debug_stage = 8;
            if (!this.p_crystal) {
                if (this.timer < this.crystal_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                if (crystal != 999) {
                    PistonAura.mc.player.inventory.currentItem = crystal;
                }
                PistonAura.mc.playerController.updateController();
                AutoCrystal.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.b_crystal, EnumFacing.UP, PistonAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                this.p_crystal = true;
            }
            this.debug_stage = 9;
            if (!this.p_redstone) {
                if (this.timer < this.place_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                PistonAura.mc.player.inventory.currentItem = redstone2;
                PistonAura.mc.playerController.updateController();
                BlockUtil.placeBlock(this.b_redStone, EnumHand.MAIN_HAND, true, this.packetPlace.getValue(), false);
                this.p_redstone = true;
            }
            this.debug_stage = 10;
            if (this.p_crystal && this.p_piston && this.p_redstone && !this.u_crystal) {
                if (this.timer < this.break_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                Entity t_crystal = PistonAura.mc.world.loadedEntityList.stream().filter(p -> p instanceof EntityEnderCrystal).min(Comparator.comparing(c -> Float.valueOf(this.target.getDistance(c)))).orElse(null);
                if (t_crystal == null) {
                    if (this.attempts < this.break_attempts.getValue()) {
                        ++this.attempts;
                        return;
                    }
                    this.attempts = 0;
                    Command.sendMessage("Not found crystal! retrying...");
                    this.r_redstone = true;
                    this.b_stage = 0;
                    return;
                }
                this.crystalId = t_crystal.entityId;
                if (this.antiweakness.getValue().booleanValue()) {
                    PistonAura.mc.player.inventory.currentItem = sword;
                    PistonAura.mc.playerController.updateController();
                }
                PistonAura.mc.player.connection.sendPacket((Packet) new CPacketUseEntity(t_crystal));
                if (this.swingArm.getValue() != arm.None) {
                    PistonAura.mc.player.swingArm(this.swingArm.getValue() == arm.MainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                }
                this.u_crystal = true;
            }
            this.debug_stage = 11;
            if (this.u_crystal) {
                if (this.timer < this.break_delay.getValue()) {
                    ++this.timer;
                    return;
                }
                this.timer = 0;
                this.Init();
                return;
            }
            PistonAura.mc.player.inventory.currentItem = oldslot;
            PistonAura.mc.playerController.updateController();
        } catch (Exception ex) {
            Command.sendMessage("Has Error! : " + ex.toString());
            Command.sendMessage("Stage : " + this.debug_stage);
            Command.sendMessage("Trying to init...");
            this.Init();
            return;
        }
    }

    private int findMaterials(Block b) {
        for (int i = 0; i < 9; ++i) {
            if (!(PistonAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) || ((ItemBlock) PistonAura.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() != b)
                continue;
            return i;
        }
        return -1;
    }

    private int findItem(Item item) {
        if (item == Items.END_CRYSTAL && PistonAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return 999;
        }
        for (int i = 0; i < 9; ++i) {
            if (PistonAura.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    private void searchSpace() {
        BlockPos floored_pos = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        BlockPos[] offset = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
        this.debug_stage = -2;
        for (int y = 0; y < this.MaxY.getValue() + 1; ++y) {
            for (int i = 0; i < offset.length; ++i) {
                this.sp(offset[i], y, floored_pos);
            }
        }
        this.debug_stage = -3;
        this.b_crystal = this.c_crystal.stream().min(Comparator.comparing(b -> PistonAura.mc.player.getDistance((double) b.getX(), (double) b.getY(), (double) b.getZ()))).orElse(null);
        this.b_piston = this.c_piston.stream().min(Comparator.comparing(b -> this.b_crystal.distanceSq((Vec3i) b))).orElse(null);
        if (this.b_piston != null) {
            this.b_redStone = this.c_redStone.stream().filter(b -> this.b_piston.getDistance(b.getX(), b.getY(), b.getZ()) < 2.0).min(Comparator.comparing(b -> this.b_crystal.distanceSq((Vec3i) b))).orElse(null);
        }
        this.debug_stage = -4;
        if (this.b_crystal == null) {
            return;
        }
        if (this.getBlock(this.b_crystal.add(0, 1, 0)).getBlock() == Blocks.PISTON_HEAD && this.getBlock(this.b_redStone).getBlock() == this.getRedStoneBlock()) {
            this.r_redstone = true;
            this.b_stage = 0;
        }
        this.debug_stage = -5;
    }

    private void sp(BlockPos offset, int offset_y, BlockPos enemy_pos) {
        BlockPos mypos = new BlockPos(PistonAura.mc.player.posX, PistonAura.mc.player.posY, PistonAura.mc.player.posZ);
        boolean v_crystal = false;
        boolean v_piston = false;
        boolean v_redstone = false;
        BlockPos pre_crystal = new BlockPos(enemy_pos.getX() + offset.getX(), enemy_pos.getY() + offset.getY() + offset_y, enemy_pos.getZ() + offset.getZ());
        BlockPos pre_piston = new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 2);
        BlockPos pre_redstone = new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 3);
        if (this.checkBlock(this.getBlock(pre_crystal).getBlock()) && this.isAir(this.getBlock(pre_crystal.add(0, 1, 0)).getBlock()) && this.isAir(this.getBlock(pre_crystal.add(0, 2, 0)).getBlock())) {
            v_crystal = true;
        }
        if (this.isAir(this.getBlock(pre_piston).getBlock())) {
            v_piston = true;
        }
        if (this.isTorch) {
            BlockPos[] t_offset = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
            ArrayList<BlockPos> pre_redstone_place = new ArrayList<BlockPos>();
            Object tmp = null;
            for (int o = 0; o < t_offset.length; ++o) {
                BlockPos pre_redstone_offset = pre_piston.add((Vec3i) t_offset[o]);
                if (!this.isAir(this.getBlock(pre_redstone_offset).getBlock()) || pre_redstone_offset.getX() == mypos.getX() && (pre_redstone_offset.getY() == mypos.getY() || pre_redstone_offset.getY() == mypos.getY() + 1) && pre_redstone_offset.getZ() == mypos.getZ() || !(pre_crystal.getDistance(pre_redstone_offset.getX(), pre_redstone_offset.getY(), pre_redstone_offset.getZ()) > 1.0))
                    continue;
                pre_redstone_place.add(pre_redstone_offset);
            }
            if (this.getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.AIR && this.getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 1, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.OBSIDIAN) {
                pre_redstone_place.add(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3));
            }
            if (this.getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 2)).getBlock() == Blocks.AIR && this.getBlock(new BlockPos(enemy_pos.getX() + offset.getX() * 3, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 3)).getBlock() == Blocks.OBSIDIAN) {
                pre_redstone_place.add(new BlockPos(enemy_pos.getX() + offset.getX() * 2, enemy_pos.getY() + offset.getY() + offset_y + 2, enemy_pos.getZ() + offset.getZ() * 2));
            }
            if ((pre_redstone = (BlockPos) pre_redstone_place.stream().min(Comparator.comparing(b -> PistonAura.mc.player.getDistance((double) b.getX(), (double) b.getY(), (double) b.getZ()))).orElse(null)) != null) {
                v_redstone = true;
            }
        } else if (this.isAir(this.getBlock(pre_redstone).getBlock())) {
            v_redstone = true;
        }
        if (pre_piston.getX() == mypos.getX() && (pre_piston.getY() == mypos.getY() || pre_piston.getY() == mypos.getY() + 1) && pre_piston.getZ() == mypos.getZ()) {
            v_piston = false;
        }
        if (pre_redstone != null && pre_redstone.getX() == mypos.getX() && (pre_redstone.getY() == mypos.getY() || pre_redstone.getY() == mypos.getY() + 1) && pre_redstone.getZ() == mypos.getZ()) {
            v_redstone = false;
        }
        if (mypos.getDistance(pre_piston.getX(), mypos.getY(), pre_piston.getZ()) < 3.1 && pre_piston.getY() > mypos.getY() + 1) {
            v_piston = false;
        }
        if (this.getBlock(pre_crystal.add(0, 1, 0)).getBlock() == Blocks.PISTON_HEAD && (this.getBlock(pre_redstone).getBlock() == Blocks.REDSTONE_BLOCK || this.getBlock(pre_redstone).getBlock() == Blocks.REDSTONE_TORCH)) {
            v_piston = true;
            v_crystal = true;
            v_redstone = true;
        }
        if (v_crystal && v_piston && v_redstone) {
            this.c_crystal.add(pre_crystal);
            this.c_piston.add(pre_piston);
            this.c_redStone.add(pre_redstone);
        }
    }

    private void Init() {
        this.target = null;
        this.b_crystal = null;
        this.b_piston = null;
        this.b_redStone = null;
        this.c_crystal = new ArrayList<BlockPos>();
        this.c_piston = new ArrayList<BlockPos>();
        this.c_redStone = new ArrayList<BlockPos>();
        this.p_crystal = false;
        this.p_piston = false;
        this.p_redstone = false;
        this.u_crystal = false;
        this.attempts = 0;
        this.r_redstone = false;
        this.b_stage = 0;
        this.trapprogress = 0;
        this.timer = 0;
        this.crystalId = 0;
        this.debug_stage = -1;
    }

    private float getRange(BlockPos t) {
        return (float) PistonAura.mc.player.getDistance((double) t.getX(), (double) t.getY(), (double) t.getZ());
    }

    private boolean isAir(Block b) {
        return b == Blocks.AIR;
    }

    private boolean checkBlock(Block b) {
        return b == Blocks.OBSIDIAN || b == Blocks.BEDROCK;
    }

    private IBlockState getBlock(BlockPos o) {
        return PistonAura.mc.world.getBlockState(o);
    }

    private double f(double v) {
        return Math.floor(v);
    }

    private Block getRedStoneBlock() {
        return this.isTorch ? Blocks.REDSTONE_TORCH : Blocks.REDSTONE_BLOCK;
    }

    private static enum mode {
        Smart,
        Force,
        None;

    }

    private static enum redstone {
        Block,
        Torch,
        Both;

    }

    private static enum arm {
        MainHand,
        OffHand,
        None;

    }

    private static enum types {
        Nearest,
        Looking;

    }
}

