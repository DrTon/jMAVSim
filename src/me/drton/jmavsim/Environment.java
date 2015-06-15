package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 28.11.13 Time: 20:35
 */
public abstract class Environment extends WorldObject {
    public Environment(World world) {
        super(world);
    }

    /**
     * Get gravity vector.
     * Should be pointed to ground.
     *
     * @return gravity vector
     */
    public abstract Vector3d getG();

    /**
     * Get magnetic field vector in specified point.
     *
     * @param point point in NED frame
     * @return magnetic field vector
     */
    public abstract Vector3d getMagField(Vector3d point);

    /**
     * Get wind (air velocity) vector in specified point.
     *
     * @param point point in NED frame
     * @return wind vector
     */
    public abstract Vector3d getWind(Vector3d point);

    /**
     * Get ground level for specified point.
     * Multilevel environment may be simulated, in this case method should return level under specified point.
     *
     * @param point point in NED frame
     * @return ground level in NED frame
     */
    public abstract double getGroundLevel(Vector3d point);
}
