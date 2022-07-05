package dev._3000IQPlay.experium.features.modules.movement;

import java.util.ArrayList;

import dev._3000IQPlay.experium.features.setting.Setting;
import dev._3000IQPlay.experium.features.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Anchor
        extends Module {
    public static boolean AnchorING;
    private final ArrayList<BlockPos> holes = new ArrayList();
    public Setting<Boolean> pull = this.register(new Setting<Boolean>("Pull", true));
    public Setting<Integer> pitch = this.register(new Setting<Integer>("Pitch", 60, 0, 90));
    int holeblocks;
    private Vec3d Center = Vec3d.ZERO;

    public Anchor() {
        super("Anchor", "Stops all movement if player is above a hole.", Module.Category.MOVEMENT, false, false, false);
    }

    public boolean isBlockHole(BlockPos blockpos) {
        this.holeblocks = 0;
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        if (Anchor.mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || Anchor.mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }
        return this.holeblocks >= 9;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5;
        return new Vec3d(x, y, z);
    }

    @Override
    @SubscribeEvent
    public void onUpdate() {
        if (Anchor.mc.world == null) {
            return;
        }
        if (Anchor.mc.player.posY < 0.0) {
            return;
        }
        if (Anchor.mc.player.rotationPitch >= (float) this.pitch.getValue().intValue()) {
            if (this.isBlockHole(this.getPlayerPos().down(1)) || this.isBlockHole(this.getPlayerPos().down(2)) || this.isBlockHole(this.getPlayerPos().down(3)) || this.isBlockHole(this.getPlayerPos().down(4))) {
                AnchorING = true;
                if (!this.pull.getValue().booleanValue()) {
                    Anchor.mc.player.motionX = 0.0;
                    Anchor.mc.player.motionZ = 0.0;
                } else {
                    this.Center = this.GetCenter(Anchor.mc.player.posX, Anchor.mc.player.posY, Anchor.mc.player.posZ);
                    double XDiff = Math.abs(this.Center.x - Anchor.mc.player.posX);
                    double ZDiff = Math.abs(this.Center.z - Anchor.mc.player.posZ);
                    if (XDiff <= 0.1 && ZDiff <= 0.1) {
                        this.Center = Vec3d.ZERO;
                    } else {
                        double MotionX = this.Center.x - Anchor.mc.player.posX;
                        double MotionZ = this.Center.z - Anchor.mc.player.posZ;
                        Anchor.mc.player.motionX = MotionX / 2.0;
                        Anchor.mc.player.motionZ = MotionZ / 2.0;
                    }
                }
            } else {
                AnchorING = false;
            }
        }
    }

    @Override
    public void onDisable() {
        AnchorING = false;
        this.holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Anchor.mc.player.posX), Math.floor(Anchor.mc.player.posY), Math.floor(Anchor.mc.player.posZ));
    }
}

