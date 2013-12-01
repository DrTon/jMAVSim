package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.Vehicle;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 13:32
 */
public interface Sensors {
    void setVehicle(Vehicle vehicle);

    Vector3d getAcc();

    Vector3d getGyro();

    Vector3d getMag();

    double getPressureAlt();
}
