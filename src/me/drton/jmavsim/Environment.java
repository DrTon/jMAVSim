package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 28.11.13 Time: 20:35
 */
public interface Environment {
    Vector3d getG();

    Vector3d getMagField(Vector3d point);

    Vector3d getWind(Vector3d point);

    double getGroundLevel(Vector3d point);
}
