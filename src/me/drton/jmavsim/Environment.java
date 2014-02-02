package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 28.11.13 Time: 20:35
 */
public abstract class Environment extends WorldObject {
    public Environment(World world) {
        super(world);
    }

    public abstract Vector3d getG();

    public abstract Vector3d getMagField(Vector3d point);

    public abstract Vector3d getWind(Vector3d point);

    public abstract double getGroundLevel(Vector3d point);
}
