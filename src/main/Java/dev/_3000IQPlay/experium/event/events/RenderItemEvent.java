package dev._3000IQPlay.experium.event.events;

import dev._3000IQPlay.experium.event.EventStage;

public class RenderItemEvent
        extends EventStage {
    double mainX;
    double mainY;
    double mainZ;
    double offX;
    double offY;
    double offZ;
    double mainRotX;
    double mainRotY;
    double mainRotZ;
    double offRotX;
    double offRotY;
    double offRotZ;
    double mainHandScaleX;
    double mainHandScaleY;
    double mainHandScaleZ;
    double offHandScaleX;
    double offHandScaleY;
    double offHandScaleZ;

    public RenderItemEvent(double mainX, double mainY, double mainZ, double offX, double offY, double offZ, double mainRotX, double mainRotY, double mainRotZ, double offRotX, double offRotY, double offRotZ, double mainHandScaleX, double mainHandScaleY, double mainHandScaleZ, double offHandScaleX, double offHandScaleY, double offHandScaleZ) {
        this.mainX = mainX;
        this.mainY = mainY;
        this.mainZ = mainZ;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        this.mainRotX = mainRotX;
        this.mainRotY = mainRotY;
        this.mainRotZ = mainRotZ;
        this.offRotX = offRotX;
        this.offRotY = offRotY;
        this.offRotZ = offRotZ;
        this.mainHandScaleX = mainHandScaleX;
        this.mainHandScaleY = mainHandScaleY;
        this.mainHandScaleZ = mainHandScaleZ;
        this.offHandScaleX = offHandScaleX;
        this.offHandScaleY = offHandScaleY;
        this.offHandScaleZ = offHandScaleZ;
    }

    public double getMainX() {
        return this.mainX;
    }

    public void setMainX(double v) {
        this.mainX = v;
    }

    public double getMainY() {
        return this.mainY;
    }

    public void setMainY(double v) {
        this.mainY = v;
    }

    public double getMainZ() {
        return this.mainZ;
    }

    public void setMainZ(double v) {
        this.mainZ = v;
    }

    public double getOffX() {
        return this.offX;
    }

    public void setOffX(double v) {
        this.offX = v;
    }

    public double getOffY() {
        return this.offY;
    }

    public void setOffY(double v) {
        this.offY = v;
    }

    public double getOffZ() {
        return this.offZ;
    }

    public void setOffZ(double v) {
        this.offZ = v;
    }

    public double getMainRotX() {
        return this.mainRotX;
    }

    public void setMainRotX(double v) {
        this.mainRotX = v;
    }

    public double getMainRotY() {
        return this.mainRotY;
    }

    public void setMainRotY(double v) {
        this.mainRotY = v;
    }

    public double getMainRotZ() {
        return this.mainRotZ;
    }

    public void setMainRotZ(double v) {
        this.mainRotZ = v;
    }

    public double getOffRotX() {
        return this.offRotX;
    }

    public void setOffRotX(double v) {
        this.offRotX = v;
    }

    public double getOffRotY() {
        return this.offRotY;
    }

    public void setOffRotY(double v) {
        this.offRotY = v;
    }

    public double getOffRotZ() {
        return this.offRotZ;
    }

    public void setOffRotZ(double v) {
        this.offRotZ = v;
    }

    public double getMainHandScaleX() {
        return this.mainHandScaleX;
    }

    public void setMainHandScaleX(double v) {
        this.mainHandScaleX = v;
    }

    public double getMainHandScaleY() {
        return this.mainHandScaleY;
    }

    public void setMainHandScaleY(double v) {
        this.mainHandScaleY = v;
    }

    public double getMainHandScaleZ() {
        return this.mainHandScaleZ;
    }

    public void setMainHandScaleZ(double v) {
        this.mainHandScaleZ = v;
    }

    public double getOffHandScaleX() {
        return this.offHandScaleX;
    }

    public void setOffHandScaleX(double v) {
        this.offHandScaleX = v;
    }

    public double getOffHandScaleY() {
        return this.offHandScaleY;
    }

    public void setOffHandScaleY(double v) {
        this.offHandScaleY = v;
    }

    public double getOffHandScaleZ() {
        return this.offHandScaleZ;
    }

    public void setOffHandScaleZ(double v) {
        this.offHandScaleZ = v;
    }
}

