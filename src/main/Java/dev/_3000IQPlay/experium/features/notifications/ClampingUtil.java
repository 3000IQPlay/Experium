package dev._3000IQPlay.experium.features.notifications;

public class ClampingUtil {
    /**
     * Clamps the value given.
     * @param value Value of the number that you want to be clamped.
     * @param minimum Minimum value.
     * @param maximum Maximum value.
     * @return Clamped value.
     * @author noat
     */
    public static double clamp(double value, double minimum, double maximum) {
        if (value > maximum) return maximum;
        return Math.max(value, minimum);
    }
}