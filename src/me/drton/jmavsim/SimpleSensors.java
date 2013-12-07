package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.Vehicle;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 27.11.13 Time: 19:06
 */
public class SimpleSensors implements Sensors {
    private Vehicle vehicle;
    private GlobalPositionProjector gpsProjector = new GlobalPositionProjector();

    public void initGPS(double lat, double lon) {
        gpsProjector.init(lat, lon);
    }

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
        Vector3d mag = new Vector3d(vehicle.getEnvironment().getMagField(vehicle.getPosition()));
        Matrix3d rot = new Matrix3d(vehicle.getRotation());
        rot.transpose();
        rot.transform(mag);
        return mag;
    }

    @Override
    public double getPressureAlt() {
        return -vehicle.getPosition().z;
    }

    @Override
    public GPSPosition getGPS() {
        double[] latlon = gpsProjector.reproject(vehicle.getPosition().x, vehicle.getPosition().y);
        GPSPosition gps = new GPSPosition();
        gps.lat = latlon[0];
        gps.lon = latlon[1];
        gps.alt = -vehicle.getPosition().z;
        gps.eph = 1.0;
        gps.epv = 1.0;
        gps.vn = vehicle.getVelocity().x;
        gps.ve = vehicle.getVelocity().y;
        gps.vd = vehicle.getVelocity().z;
        return gps;
    }
}
