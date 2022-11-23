package dev._3000IQPlay.experium.mixin.mixins.accessors;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityPlayerSP.class})
public interface IEntityPlayerSP {
    @Accessor(value="serverSneakState")
    public void setServerSneakState(boolean var1);

    @Accessor(value="serverSneakState")
    public boolean getServerSneakState();

    @Accessor(value="serverSprintState")
    public void setServerSprintState(boolean var1);

    @Accessor(value="serverSprintState")
    public boolean getServerSprintState();

    @Accessor(value="prevOnGround")
    public void setPrevOnGround(boolean var1);

    @Accessor(value="prevOnGround")
    public boolean getPrevOnGround();

    @Accessor(value="autoJumpEnabled")
    public void setAutoJumpEnabled(boolean var1);

    @Accessor(value="autoJumpEnabled")
    public boolean getAutoJumpEnabled();

    @Accessor(value="lastReportedPosX")
    public void setLastReportedPosX(double var1);

    @Accessor(value="lastReportedPosX")
    public double getLastReportedPosX();

    @Accessor(value="lastReportedPosY")
    public void setLastReportedPosY(double var1);

    @Accessor(value="lastReportedPosY")
    public double getLastReportedPosY();

    @Accessor(value="lastReportedPosZ")
    public void setLastReportedPosZ(double var1);

    @Accessor(value="lastReportedPosZ")
    public double getLastReportedPosZ();

    @Accessor(value="lastReportedYaw")
    public void setLastReportedYaw(float var1);

    @Accessor(value="lastReportedYaw")
    public float getLastReportedYaw();

    @Accessor(value="lastReportedPitch")
    public void setLastReportedPitch(float var1);

    @Accessor(value="lastReportedPitch")
    public float getLastReportedPitch();

    @Accessor(value="positionUpdateTicks")
    public void setPositionUpdateTicks(int var1);

    @Accessor(value="positionUpdateTicks")
    public int getPositionUpdateTicks();

    @Invoker(value="onUpdateWalkingPlayer")
    public void invokeOnUpdateWalkingPlayer();
}