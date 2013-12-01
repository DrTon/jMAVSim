package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;
import me.drton.jmavsim.Sensors;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 13:31
 */
public interface Vehicle {
    void update(long t);

    Environment getEnvironment();

    Vector3d getPosition();

    Vector3d getVelocity();

    Vector3d getAcceleration();

    Matrix3d getRotation();

    Vector3d getRotationRate();

    void setControl(double[] control);

    Sensors getSensors();
}
