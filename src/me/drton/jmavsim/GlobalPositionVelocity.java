package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 01.12.13 Time: 22:25
 */
public class GlobalPositionVelocity {
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
