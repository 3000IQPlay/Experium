package dev._3000IQPlay.experium.features.notifications;

import dev._3000IQPlay.experium.features.notifications.BackInOutEasing;
import dev._3000IQPlay.experium.features.notifications.ClampingUtil;

/**
 * Simple to use animation.
 * @author noat
 * @since 13.10.22
 */
public class Animation {
    boolean state;
    float duration;
    BackInOutEasing easing;
    long lastMs;

    public Animation(float duration, boolean initialState, BackInOutEasing easing) {
        this.duration = duration;
        setState(initialState);
        this.easing = easing;
    }

    public void setState(boolean bool) {
        lastMs = (long)(!bool ? System.currentTimeMillis() - ((1 - getLinearFactor()) * (long) (duration)) : System.currentTimeMillis() - (getLinearFactor() * (long) duration));
        state = bool;
    }

    public double getLinearFactor()  {
        return state ? ClampingUtil.clamp(1 - (System.currentTimeMillis() - (double) lastMs) / (double) duration, 0.0, 1.0) : ClampingUtil.clamp((System.currentTimeMillis() - (double) lastMs) / (double) duration, 0.0, 1.0);
    }

    public double getAnimationFactor() {
        return state ? easing.invoke(ClampingUtil.clamp((System.currentTimeMillis() - lastMs) / (double) duration, 0.0, 1.0)) : easing.invoke(ClampingUtil.clamp(1 - (System.currentTimeMillis() - (double) lastMs) / (double) duration, 0.0, 1.0));
    }
}