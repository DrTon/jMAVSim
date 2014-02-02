package me.drton.jmavsim;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 27.11.13 Time: 19:06
 */
public class SimpleSensors implements Sensors {
    private MechanicalObject object;
    private GlobalPositionProjector gpsProjector = new GlobalPositionProjector();

    public void initGPS(double lat, double lon) {
        gpsProjector.init(lat, lon);
    }

    @Override
    public void setObject(MechanicalObject object) {
        this.object = object;
    }

    @Override
    public Vector3d getAcc() {
        Vector3d accBody = new Vector3d(object.getAcceleration());
        accBody.sub(object.getWorld().getEnvironment().getG());
        Matrix3d rot = new Matrix3d(object.getRotation());
        rot.transpose();
        rot.transform(accBody);
        return accBody;
    }

    @Override
    public Vector3d getGyro() {
        return object.getRotationRate();
    }

    @Override
    public Vector3d getMag() {
        Vector3d mag = new Vector3d(object.getWorld().getEnvironment().getMagField(object.getPosition()));
        Matrix3d rot = new Matrix3d(object.getRotation());
        rot.transpose();
        rot.transform(mag);
        return mag;
    }

    @Override
    public double getPressureAlt() {
        return -object.getPosition().z;
    }

    @Override
    public GPSPosition getGPS() {
        double[] latlon = gpsProjector.reproject(object.getPosition().x, object.getPosition().y);
        GPSPosition gps = new GPSPosition();
        gps.lat = latlon[0];
        gps.lon = latlon[1];
        gps.alt = -object.getPosition().z;
        gps.eph = 1.0;
        gps.epv = 1.0;
        gps.vn = object.getVelocity().x;
        gps.ve = object.getVelocity().y;
        gps.vd = object.getVelocity().z;
        return gps;
    }
}
