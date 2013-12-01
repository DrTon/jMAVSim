package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.Vehicle;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 27.11.13 Time: 19:06
 */
public class SimpleSensors implements Sensors {
    private Vehicle vehicle;
    private Vector3d magField = new Vector3d(0.2, 0.0, -0.8);

    @Override
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public Vector3d getAcc() {
        Vector3d accBody = new Vector3d(vehicle.getAcceleration());
        accBody.sub(vehicle.getEnvironment().getG());
        Matrix3d rot = new Matrix3d(vehicle.getRotation());
        rot.transpose();
        rot.transform(accBody);
        return accBody;
    }

    @Override
    public Vector3d getGyro() {
        return vehicle.getRotationRate();
    }

    @Override
    public Vector3d getMag() {
        Vector3d mag = new Vector3d(magField);
        Matrix3d rot = new Matrix3d(vehicle.getRotation());
        rot.transpose();
        rot.transform(mag);
        return mag;
    }

    @Override
    public double getPressureAlt() {
        return -vehicle.getPosition().z;
    }
}
