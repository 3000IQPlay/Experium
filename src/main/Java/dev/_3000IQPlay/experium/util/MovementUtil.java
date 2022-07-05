package dev._3000IQPlay.experium.util;

import java.util.Objects;

import dev._3000IQPlay.experium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MovementUtil
        implements Util {
    public static Vec3d calculateLine(Vec3d x1, Vec3d x2, double distance) {
        double length = Math.sqrt(MovementUtil.multiply(x2.x - x1.x) + MovementUtil.multiply(x2.y - x1.y) + MovementUtil.multiply(x2.z - x1.z));
        double unitSlopeX = (x2.x - x1.x) / length;
        double unitSlopeY = (x2.y - x1.y) / length;
        double unitSlopeZ = (x2.z - x1.z) / length;
        double x = x1.x + unitSlopeX * distance;
        double y = x1.y + unitSlopeY * distance;
        double z = x1.z + unitSlopeZ * distance;
        return new Vec3d(x, y, z);
    }

    public static Vec2f calculateLineNoY(Vec2f x1, Vec2f x2, double distance) {
        double length = Math.sqrt(MovementUtil.multiply(x2.x - x1.x) + MovementUtil.multiply(x2.y - x1.y));
        double unitSlopeX = (double) (x2.x - x1.x) / length;
        double unitSlopeZ = (double) (x2.y - x1.y) / length;
        float x = (float) ((double) x1.x + unitSlopeX * distance);
        float z = (float) ((double) x1.y + unitSlopeZ * distance);
        return new Vec2f(x, z);
    }

    public static double multiply(double one) {
        return one * one;
    }
	
	public static double[] forward(double d) {
        float f = Minecraft.getMinecraft().player.movementInput.moveForward;
        float f2 = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float f3 = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += (float)(f > 0.0f ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += (float)(f > 0.0f ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        double d4 = (double)f * d * d3 + (double)f2 * d * d2;
        double d5 = (double)f * d * d2 - (double)f2 * d * d3;
        return new double[]{d4, d5};
    }
	
	public static boolean isMoving(EntityLivingBase entityLivingBase) {
        return entityLivingBase.moveForward != 0.0f || entityLivingBase.moveStrafing != 0.0f;
    }

    public static void setSpeed(EntityLivingBase entityLivingBase, double d) {
        double[] dArray = MovementUtil.forward(d);
        entityLivingBase.motionX = dArray[0];
        entityLivingBase.motionZ = dArray[1];
    }

    public static double getBaseMoveSpeed() {
        double d = 0.2873;
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isPotionActive(Objects.requireNonNull(Potion.getPotionById((int)1)))) {
            int n = Objects.requireNonNull(Minecraft.getMinecraft().player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById((int)1)))).getAmplifier();
            d *= 1.0 + 0.2 * (double)(n + 1);
        }
        return d;
    }
	
	public static void setMotion(double x, double y, double z) {
        if (MovementUtil.mc.player != null) {
            if (MovementUtil.mc.player.isRiding()) {
                MovementUtil.mc.player.ridingEntity.motionX = x;
                MovementUtil.mc.player.ridingEntity.motionY = y;
                MovementUtil.mc.player.ridingEntity.motionZ = x;
            } else {
                MovementUtil.mc.player.motionX = x;
                MovementUtil.mc.player.motionY = y;
                MovementUtil.mc.player.motionZ = z;
            }
        }
    }

    public static Vec3d extrapolatePlayerPositionWithGravity(EntityPlayer player, int ticks) {
        double totalDistance = 0.0;
        double extrapolatedMotionY = player.motionY;
        for (int i = 0; i < ticks; ++i) {
            totalDistance += MovementUtil.multiply(player.motionX) + MovementUtil.multiply(extrapolatedMotionY) + MovementUtil.multiply(player.motionZ);
            extrapolatedMotionY -= 0.1;
        }
        double horizontalDistance = MovementUtil.multiply(player.motionX) + MovementUtil.multiply(player.motionZ) * (double) ticks;
        Vec2f horizontalVec = MovementUtil.calculateLineNoY(new Vec2f((float) player.lastTickPosX, (float) player.lastTickPosZ), new Vec2f((float) player.posX, (float) player.posZ), horizontalDistance);
        double addedY = player.motionY;
        double finalY = player.posY;
        Vec3d tempPos = new Vec3d(horizontalVec.x, player.posY, horizontalVec.y);
        for (int i = 0; i < ticks; ++i) {
            finalY += addedY;
            addedY -= 0.1;
        }
        RayTraceResult result = MovementUtil.mc.world.rayTraceBlocks(player.getPositionVector(), new Vec3d(tempPos.x, finalY, tempPos.z));
        if (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) {
            return new Vec3d(tempPos.x, finalY, tempPos.z);
        }
        return result.hitVec;
    }

    public static Vec3d extrapolatePlayerPosition(EntityPlayer player, int ticks) {
        double totalDistance = 0.0;
        double extrapolatedMotionY = player.motionY;
        for (int i = 0; i < ticks; ++i) {
        }
        Vec3d lastPos = new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
        Vec3d currentPos = new Vec3d(player.posX, player.posY, player.posZ);
        double distance = MovementUtil.multiply(player.motionX) + MovementUtil.multiply(player.motionY) + MovementUtil.multiply(player.motionZ);
        double extrapolatedPosY = player.posY;
        if (!player.hasNoGravity()) {
            extrapolatedPosY -= 0.1;
        }
        Vec3d tempVec = MovementUtil.calculateLine(lastPos, currentPos, distance * (double) ticks);
        Vec3d finalVec = new Vec3d(tempVec.x, extrapolatedPosY, tempVec.z);
        RayTraceResult result = MovementUtil.mc.world.rayTraceBlocks(player.getPositionVector(), finalVec);
        return new Vec3d(tempVec.x, player.posY, tempVec.z);
    }
}