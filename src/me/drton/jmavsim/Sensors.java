package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 13:32
 */
public interface Sensors {
    void setObject(DynamicObject object);

    Vector3d getAcc();

    Vector3d getGyro();

    Vector3d getMag();

    double getPressureAlt();

    GNSSReport getGNSS();

    boolean isGPSUpdated();

    void update(long t);
}
