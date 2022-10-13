package dev._3000IQPlay.experium.features.notifications;

public class BackInOutEasing {
    public double invoke(double x) {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }
}