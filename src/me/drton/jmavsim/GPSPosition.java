package me.drton.jmavsim;

import me.drton.jmavlib.geo.LatLonAlt;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 01.12.13 Time: 22:25
 */
public class GPSPosition {
    public LatLonAlt position;
    public double eph;
    public double epv;
    public Vector3d velocity;
    public int fix;
    public long time;

    public double getSpeed() {
        return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
    }

    public double getCog() {
        return Math.atan2(velocity.y, velocity.x);
    }
}
