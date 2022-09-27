package dev._3000IQPlay.experium.manager;

import dev._3000IQPlay.experium.features.Feature;

public class MovementManager
        extends Feature {
    public void setMotion(double d, double d2, double d3) {
        if (MovementManager.mc.player != null) {
            if (MovementManager.mc.player.isRiding()) {
                MovementManager.mc.player.ridingEntity.motionX = d;
                MovementManager.mc.player.ridingEntity.motionY = d2;
                MovementManager.mc.player.ridingEntity.motionZ = d;
            } else {
                MovementManager.mc.player.motionX = d;
                MovementManager.mc.player.motionY = d2;
                MovementManager.mc.player.motionZ = d3;
            }
        }
    }
}