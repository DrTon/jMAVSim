package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 21.03.14 Time: 23:22
 */
public class CameraGimbal2D extends MechanicalObject {
    private MechanicalObject baseObject;

    public CameraGimbal2D(World world) {
        super(world);
    }

    public void setBaseObject(MechanicalObject object) {
        this.baseObject = object;
    }

    @Override
    public void update(long t) {
        this.position = baseObject.position;
        this.velocity = baseObject.velocity;
        this.acceleration = baseObject.acceleration;
        double yaw = Math.atan2(baseObject.rotation.getElement(1, 0), baseObject.rotation.getElement(0, 0));
        this.rotation.rotZ(yaw);
    }

    @Override
    protected Vector3d getForce() {
        return null;
    }

    @Override
    protected Vector3d getTorque() {
        return null;
    }
}
