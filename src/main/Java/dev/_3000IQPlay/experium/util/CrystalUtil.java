package dev._3000IQPlay.experium.util;

import dev._3000IQPlay.experium.features.modules.combat.CrystalAura;
import dev._3000IQPlay.experium.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class CrystalUtil {
	public static Minecraft mc;
    private static List<Block> valid;

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d vec3d, Vec3d vec3d2, boolean bl, boolean bl2, boolean bl3) {
        if (!(Double.isNaN(vec3d.x) || Double.isNaN(vec3d.y) || Double.isNaN(vec3d.z))) {
            if (!(Double.isNaN(vec3d2.x) || Double.isNaN(vec3d2.y) || Double.isNaN(vec3d2.z))) {
                RayTraceResult rayTraceResult;
                int n;
                int n2;
                int n3 = MathHelper.floor((double)vec3d2.x);
                int n4 = MathHelper.floor((double)vec3d2.y);
                int n5 = MathHelper.floor((double)vec3d2.z);
                int n6 = MathHelper.floor((double)vec3d.x);
                BlockPos blockPos = new BlockPos(n6, n2 = MathHelper.floor((double)vec3d.y), n = MathHelper.floor((double)vec3d.z));
                IBlockState iBlockState = CrystalUtil.mc.world.getBlockState(blockPos);
                Block block = iBlockState.getBlock();
                if (!valid.contains((Object)block)) {
                    block = Blocks.AIR;
                    iBlockState = Blocks.AIR.getBlockState().getBaseState();
                }
                if ((!bl2 || iBlockState.getCollisionBoundingBox((IBlockAccess)CrystalUtil.mc.world, blockPos) != Block.NULL_AABB) && block.canCollideCheck(iBlockState, bl) && (rayTraceResult = iBlockState.collisionRayTrace((World)CrystalUtil.mc.world, blockPos, vec3d, vec3d2)) != null) {
                    return rayTraceResult;
                }
                RayTraceResult rayTraceResult2 = null;
                int n7 = 200;
                while (n7-- >= 0) {
                    EnumFacing enumFacing;
                    if (Double.isNaN(vec3d.x) || Double.isNaN(vec3d.y) || Double.isNaN(vec3d.z)) {
                        return null;
                    }
                    if (n6 == n3 && n2 == n4 && n == n5) {
                        return bl3 ? rayTraceResult2 : null;
                    }
                    boolean bl4 = true;
                    boolean bl5 = true;
                    boolean bl6 = true;
                    double d = 999.0;
                    double d2 = 999.0;
                    double d3 = 999.0;
                    if (n3 > n6) {
                        d = (double)n6 + 1.0;
                    } else if (n3 < n6) {
                        d = (double)n6 + 0.0;
                    } else {
                        bl4 = false;
                    }
                    if (n4 > n2) {
                        d2 = (double)n2 + 1.0;
                    } else if (n4 < n2) {
                        d2 = (double)n2 + 0.0;
                    } else {
                        bl5 = false;
                    }
                    if (n5 > n) {
                        d3 = (double)n + 1.0;
                    } else if (n5 < n) {
                        d3 = (double)n + 0.0;
                    } else {
                        bl6 = false;
                    }
                    double d4 = 999.0;
                    double d5 = 999.0;
                    double d6 = 999.0;
                    double d7 = vec3d2.x - vec3d.x;
                    double d8 = vec3d2.y - vec3d.y;
                    double d9 = vec3d2.z - vec3d.z;
                    if (bl4) {
                        d4 = (d - vec3d.x) / d7;
                    }
                    if (bl5) {
                        d5 = (d2 - vec3d.y) / d8;
                    }
                    if (bl6) {
                        d6 = (d3 - vec3d.z) / d9;
                    }
                    if (d4 == -0.0) {
                        d4 = -1.0E-4;
                    }
                    if (d5 == -0.0) {
                        d5 = -1.0E-4;
                    }
                    if (d6 == -0.0) {
                        d6 = -1.0E-4;
                    }
                    if (d4 < d5 && d4 < d6) {
                        enumFacing = n3 > n6 ? EnumFacing.WEST : EnumFacing.EAST;
                        vec3d = new Vec3d(d, vec3d.y + d8 * d4, vec3d.z + d9 * d4);
                    } else if (d5 < d6) {
                        enumFacing = n4 > n2 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec3d = new Vec3d(vec3d.x + d7 * d5, d2, vec3d.z + d9 * d5);
                    } else {
                        enumFacing = n5 > n ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec3d = new Vec3d(vec3d.x + d7 * d6, vec3d.y + d8 * d6, d3);
                    }
                    n6 = MathHelper.floor((double)vec3d.x) - (enumFacing == EnumFacing.EAST ? 1 : 0);
                    n2 = MathHelper.floor((double)vec3d.y) - (enumFacing == EnumFacing.UP ? 1 : 0);
                    n = MathHelper.floor((double)vec3d.z) - (enumFacing == EnumFacing.SOUTH ? 1 : 0);
                    blockPos = new BlockPos(n6, n2, n);
                    IBlockState iBlockState2 = CrystalUtil.mc.world.getBlockState(blockPos);
                    Block block2 = iBlockState2.getBlock();
                    if (!valid.contains((Object)block2)) {
                        block2 = Blocks.AIR;
                        iBlockState2 = Blocks.AIR.getBlockState().getBaseState();
                    }
                    if (bl2 && iBlockState2.getMaterial() != Material.PORTAL && iBlockState2.getCollisionBoundingBox((IBlockAccess)CrystalUtil.mc.world, blockPos) == Block.NULL_AABB) continue;
                    if (block2.canCollideCheck(iBlockState2, bl)) {
                        RayTraceResult rayTraceResult3 = iBlockState2.collisionRayTrace((World)CrystalUtil.mc.world, blockPos, vec3d, vec3d2);
                        if (rayTraceResult3 == null) continue;
                        return rayTraceResult3;
                    }
                    rayTraceResult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec3d, enumFacing, blockPos);
                }
                return bl3 ? rayTraceResult2 : null;
            }
            return null;
        }
        return null;
    }

    public static int ping() {
        if (mc.getConnection() == null) {
            return 50;
        }
        if (CrystalUtil.mc.player == null) {
            return 50;
        }
        try {
            return mc.getConnection().getPlayerInfo(CrystalUtil.mc.player.getUniqueID()).getResponseTime();
        }
        catch (NullPointerException nullPointerException) {
            return 50;
        }
    }

    public static float calculateDamage(BlockPos blockPos, Entity entity) {
        return CrystalUtil.calculateDamage((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5, entity);
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.add(0, 1, 0);
        BlockPos blockPos3 = blockPos.add(0, 2, 0);
        try {
            if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (CrystalUtil.mc.world.getBlockState(blockPos2).getBlock() != Blocks.AIR || CrystalUtil.mc.world.getBlockState(blockPos3).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos2))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            for (Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos3))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
        }
        catch (Exception exception) {
            return false;
        }
        return true;
    }

    public static boolean rayTracePlace(BlockPos blockPos) {
        if (CrystalAura.getInstance().directionMode.getValue() != CrystalAura.DirectionMode.VANILLA) {
            double d = 0.45;
            double d2 = 0.05;
            double d3 = 0.95;
            Vec3d vec3d = new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.getEntityBoundingBox().minY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ);
            for (double d4 = d2; d4 <= d3; d4 += d) {
                for (double d5 = d2; d5 <= d3; d5 += d) {
                    for (double d6 = d2; d6 <= d3; d6 += d) {
                        Vec3d vec3d2 = new Vec3d((Vec3i)blockPos).add(d4, d5, d6);
                        double d7 = vec3d.distanceTo(vec3d2);
                        if (CrystalAura.getInstance().strictDirection.getValue().booleanValue() && d7 > (double)CrystalAura.getInstance().placeRange.getValue().floatValue()) continue;
                        double d8 = vec3d2.x - vec3d.x;
                        double d9 = vec3d2.y - vec3d.y;
                        double d10 = vec3d2.z - vec3d.z;
                        double d11 = MathHelper.sqrt((double)(d8 * d8 + d10 * d10));
                        double[] arrd = new double[]{MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(d10, d8)) - 90.0f)), MathHelper.wrapDegrees((float)((float)(-Math.toDegrees(Math.atan2(d9, d11)))))};
                        float f = MathHelper.cos((float)((float)(-arrd[0] * 0.01745329238474369 - 3.1415927410125732)));
                        float f2 = MathHelper.sin((float)((float)(-arrd[0] * 0.01745329238474369 - 3.1415927410125732)));
                        float f3 = -MathHelper.cos((float)((float)(-arrd[1] * 0.01745329238474369)));
                        float f4 = MathHelper.sin((float)((float)(-arrd[1] * 0.01745329238474369)));
                        Vec3d vec3d3 = new Vec3d((double)(f2 * f3), (double)f4, (double)(f * f3));
                        Vec3d vec3d4 = vec3d.add(vec3d3.x * d7, vec3d3.y * d7, vec3d3.z * d7);
                        RayTraceResult rayTraceResult = CrystalUtil.mc.world.rayTraceBlocks(vec3d, vec3d4, false, false, false);
                        if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals((Object)blockPos)) continue;
                        return true;
                    }
                }
            }
            return false;
        }
        for (EnumFacing enumFacing : EnumFacing.values()) {
            RayTraceResult rayTraceResult;
            Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5 + (double)enumFacing.getDirectionVec().getX() * 0.5, (double)blockPos.getY() + 0.5 + (double)enumFacing.getDirectionVec().getY() * 0.5, (double)blockPos.getZ() + 0.5 + (double)enumFacing.getDirectionVec().getZ() * 0.5);
            if (CrystalAura.getInstance().strictDirection.getValue().booleanValue() && CrystalUtil.mc.player.getPositionVector().add(0.0, (double)CrystalUtil.mc.player.getEyeHeight(), 0.0).distanceTo(vec3d) > (double)CrystalAura.getInstance().placeRange.getValue().floatValue() || (rayTraceResult = CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), vec3d, false, true, false)) == null || !rayTraceResult.typeOfHit.equals((Object)RayTraceResult.Type.BLOCK) || !rayTraceResult.getBlockPos().equals((Object)blockPos)) continue;
            return true;
        }
        return false;
    }

    public static Vec3d getMotionVec(Entity entity, int n) {
        double d = entity.posX - entity.prevPosX;
        double d2 = entity.posZ - entity.prevPosZ;
        double d3 = 0.0;
        double d4 = 0.0;
        if (CrystalAura.getInstance().collision.getValue().booleanValue()) {
            for (int i = 1; i <= n && CrystalUtil.mc.world.getBlockState(new BlockPos(entity.posX + d * (double)i, entity.posY, entity.posZ + d2 * (double)i)).getBlock() instanceof BlockAir; ++i) {
                d3 = d * (double)i;
                d4 = d2 * (double)i;
            }
        } else {
            d3 = d * (double)n;
            d4 = d2 * (double)n;
        }
        return new Vec3d(d3, 0.0, d4);
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d vec3d, Vec3d vec3d2) {
        return CrystalUtil.rayTraceBlocks(vec3d, vec3d2, false, false, false);
    }

    public static boolean isVisible(Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.getEntityBoundingBox().minY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ);
        return CrystalUtil.mc.world.rayTraceBlocks(vec3d2, vec3d) == null;
    }

    public static void breakCrystalPacket(Entity entity) {
        CrystalUtil.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
        CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static float getBlockDensity(Vec3d vec3d, AxisAlignedBB axisAlignedBB) {
        double d = 1.0 / ((axisAlignedBB.maxX - axisAlignedBB.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((axisAlignedBB.maxY - axisAlignedBB.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((axisAlignedBB.maxZ - axisAlignedBB.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d >= 0.0 && d2 >= 0.0 && d3 >= 0.0) {
            int n = 0;
            int n2 = 0;
            float f = 0.0f;
            while (f <= 1.0f) {
                float f2 = 0.0f;
                while (f2 <= 1.0f) {
                    float f3 = 0.0f;
                    while (f3 <= 1.0f) {
                        double d6 = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * (double)f;
                        double d7 = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * (double)f2;
                        double d8 = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * (double)f3;
                        if (CrystalUtil.rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec3d) == null) {
                            ++n;
                        }
                        ++n2;
                        f3 = (float)((double)f3 + d3);
                    }
                    f2 = (float)((double)f2 + d2);
                }
                f = (float)((double)f + d);
            }
            return (float)n / (float)n2;
        }
        return 0.0f;
    }

    public static float getBlastReduction(EntityLivingBase entityLivingBase, float f, Explosion explosion) {
        float f2 = f;
        if (entityLivingBase instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
            DamageSource damageSource = DamageSource.causeExplosionDamage((Explosion)explosion);
            f2 = CombatRules.getDamageAfterAbsorb((float)f2, (float)entityPlayer.getTotalArmorValue(), (float)((float)entityPlayer.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
            int n = 0;
            try {
                n = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)entityPlayer.getArmorInventoryList(), (DamageSource)damageSource);
            }
            catch (Exception exception) {
                // empty catch block
            }
            float f3 = MathHelper.clamp((float)n, (float)0.0f, (float)20.0f);
            f2 *= 1.0f - f3 / 25.0f;
            if (entityLivingBase.isPotionActive(MobEffects.RESISTANCE)) {
                f2 -= f2 / 4.0f;
            }
            f2 = Math.max(f2, 0.0f);
            return f2;
        }
        f2 = CombatRules.getDamageAfterAbsorb((float)f2, (float)entityLivingBase.getTotalArmorValue(), (float)((float)entityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        return f2;
    }

    public static boolean rayTraceBreak(double d, double d2, double d3) {
        if (CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(d, d2 + 1.8, d3), false, true, false) == null) {
            return true;
        }
        if (CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(d, d2 + 1.5, d3), false, true, false) == null) {
            return true;
        }
        return CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + (double)CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(d, d2, d3), false, true, false) == null;
    }

    static {
        mc = Minecraft.getMinecraft();
        valid = Arrays.asList(new Block[]{Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL});
    }

    public static int getCrystalSlot() {
        int n = -1;
        if (Util.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            n = Util.mc.player.inventory.currentItem;
        }
        if (n == -1) {
            for (int i = 0; i < 9; ++i) {
                if (Util.mc.player.inventory.getStackInSlot(i).getItem() != Items.END_CRYSTAL) continue;
                n = i;
                break;
            }
        }
        return n;
    }

    public static int getSwordSlot() {
        int n = -1;
        if (Util.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
            n = Util.mc.player.inventory.currentItem;
        }
        if (n == -1) {
            for (int i = 0; i < 9; ++i) {
                if (Util.mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_SWORD) continue;
                n = i;
                break;
            }
        }
        return n;
    }

    public static void breakCrystal(Entity entity) {
        CrystalUtil.mc.playerController.attackEntity((EntityPlayer)CrystalUtil.mc.player, entity);
        CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static float calculateDamage2(BlockPos blockPos, Entity entity) {
        return CrystalUtil.calculateDamage(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), entity);
    }

    public static float calculateDamage(EntityEnderCrystal entityEnderCrystal, Entity entity) {
        return CrystalUtil.calculateDamage(entityEnderCrystal.posX, entityEnderCrystal.posY, entityEnderCrystal.posZ, entity);
    }

    public static Vec3d getEntityPosVec(Entity entity, int n) {
        return entity.getPositionVector().add(CrystalUtil.getMotionVec(entity, n));
    }

    public static float calculateDamage(double d, double d2, double d3, Entity entity) {
        float f = 12.0f;
        Vec3d vec3d = CrystalUtil.getEntityPosVec(entity, CrystalAura.getInstance().predictTicks.getValue() > 0 ? CrystalAura.getInstance().predictTicks.getValue() : 0);
        double d4 = vec3d.distanceTo(new Vec3d(d, d2, d3)) / (double)f;
        Vec3d vec3d2 = new Vec3d(d, d2, d3);
        double d5 = 0.0;
        try {
            d5 = CrystalAura.getInstance().terrainIgnore.getValue() != false ? (double)CrystalUtil.getBlockDensity(vec3d2, CrystalAura.getInstance().predictTicks.getValue() > 0 ? entity.getEntityBoundingBox().offset(CrystalUtil.getMotionVec(entity, CrystalAura.getInstance().predictTicks.getValue())) : entity.getEntityBoundingBox()) : (double)entity.world.getBlockDensity(vec3d2, CrystalAura.getInstance().predictTicks.getValue() > 0 ? entity.getEntityBoundingBox().offset(CrystalUtil.getMotionVec(entity, CrystalAura.getInstance().predictTicks.getValue())) : entity.getEntityBoundingBox());
        }
        catch (Exception exception) {
            // empty catch block
        }
        double d6 = (1.0 - d4) * d5;
        float f2 = (int)((d6 * d6 + d6) / 2.0 * 7.0 * (double)f + 1.0);
        double d7 = 1.0;
        if (entity instanceof EntityLivingBase) {
            d7 = CrystalUtil.getBlastReduction((EntityLivingBase)entity, CrystalUtil.getDamageMultiplied(f2), new Explosion((World)CrystalUtil.mc.world, (Entity)CrystalUtil.mc.player, d, d2, d3, 6.0f, false, true));
        }
        return (float)d7;
    }

    public static float getDamageMultiplied(float f) {
        int n = CrystalUtil.mc.world.getDifficulty().getId();
        return f * (n == 0 ? 0.0f : (n == 2 ? 1.0f : (n == 1 ? 0.5f : 1.5f)));
    }

    public static class CrystalBad
    extends RuntimeException {
        public CrystalBad() {
            this.setStackTrace(new StackTraceElement[0]);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

