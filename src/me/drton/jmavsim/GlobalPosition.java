package me.drton.jmavsim;

/**
 * User: ton Date: 01.12.13 Time: 22:25
 */
public class GlobalPosition {
    public double lat;
    public double lon;
    public double alt;
    public double eph;
    public double epv;
    public double vn;
    public double ve;
    public double vd;

    public double getSpeed() {
        return Math.sqrt(vn * vn + ve * ve);
    }

    public double getCog() {
        return Math.atan2(ve, vn);
    }
}
