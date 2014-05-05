package me.drton.jmavsim;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 27.11.13 Time: 19:06
 */
public class SimpleSensors implements Sensors {
    private DynamicObject object;
    private GlobalPositionProjector globalProjector = new GlobalPositionProjector();

    @Override
    public void setObject(DynamicObject object) {
        this.object = object;
        globalProjector.init(object.getWorld().getGlobalReference());
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
    public GlobalPositionVelocity getGlobalPosition() {
        GlobalPositionVelocity p = new GlobalPositionVelocity();
        p.position = globalProjector.reproject(object.getPosition());
        p.eph = 1.0;
        p.epv = 1.0;
        p.velocity = object.getVelocity();
        p.fix = 3;
        p.time = System.currentTimeMillis() * 1000;
        return p;
    }

    @Override
    public void update(long t) {
    }
}
