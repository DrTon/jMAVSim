package me.drton.jmavsim;

import me.drton.jmavlib.geo.GlobalPositionProjector;
import me.drton.jmavlib.processing.DelayLine;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 27.11.13 Time: 19:06
 */
public class SimpleSensors implements Sensors {
    private DynamicObject object;
    private GlobalPositionProjector globalProjector = new GlobalPositionProjector();
    private DelayLine<GNSSReport> gpsDelayLine = new DelayLine<GNSSReport>();
    private long gpsStartTime = 0;
    private long gpsInterval = 200;
    private long gpsLast = 0;
    private GNSSReport gps = new GNSSReport();
    private boolean gpsUpdated = false;

    @Override
    public void setObject(DynamicObject object) {
        this.object = object;
        globalProjector.init(object.getWorld().getGlobalReference());
    }

    public void setGPSStartTime(long time) {
        gpsStartTime = time;
    }

    public void setGPSDelay(long delay) {
        gpsDelayLine.setDelay(delay);
    }

    public void setGPSInterval(long gpsInterval) {
        this.gpsInterval = gpsInterval;
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
    public GNSSReport getGNSS() {
        return gps;
    }

    @Override
    public boolean isGPSUpdated() {
        boolean res = gpsUpdated;
        gpsUpdated = false;
        return res;
    }

    @Override
    public void update(long t) {
        // GPS
        if (t > gpsStartTime && t > gpsLast + gpsInterval) {
            gpsLast = t;
            gpsUpdated = true;
            GNSSReport gpsCurrent = new GNSSReport();
            Vector3d pos = object.getPosition();
            gpsCurrent.position = globalProjector.reproject(new double[]{pos.x, pos.y, pos.z});
            gpsCurrent.eph = 1.0;
            gpsCurrent.epv = 1.0;
            gpsCurrent.velocity = object.getVelocity();
            gpsCurrent.fix = 3;
            gpsCurrent.time = System.currentTimeMillis() * 1000;
            gps = gpsDelayLine.getOutput(t, gpsCurrent);
        }
    }
}
