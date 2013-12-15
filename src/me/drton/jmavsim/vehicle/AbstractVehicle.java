package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;
import me.drton.jmavsim.Sensors;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 16:00
 */
public abstract class AbstractVehicle implements Vehicle {
    protected double[] control = null;
    protected Environment environment;
    protected Vector3d position = new Vector3d();
    protected Vector3d velocity = new Vector3d();
    protected Vector3d acceleration = new Vector3d();
    protected Matrix3d rotation = new Matrix3d();
    protected Vector3d rotationRate = new Vector3d();
    protected long lastTime = -1;
    protected double mass = 1.0;
    protected Matrix3d momentOfInertia = new Matrix3d();
    protected Matrix3d momentOfInertiaInv = new Matrix3d();
    private Sensors sensors;

    public AbstractVehicle(Environment environment) {
        this.environment = environment;
        rotation.rotX(0);
        momentOfInertia.rotZ(0.0);
        momentOfInertiaInv.rotZ(0.0);
        control = initControl();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
        sensors.setVehicle(this);
    }

    @Override
    public Sensors getSensors() {
        return sensors;
    }

    protected abstract double[] initControl();

    @Override
    public void setControl(double[] control) {
        System.arraycopy(control, 0, this.control, 0, this.control.length);
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setMomentOfInertia(Matrix3d momentOfInertia) {
        this.momentOfInertia.set(momentOfInertia);
        this.momentOfInertiaInv.invert(momentOfInertia);
    }

    @Override
    public void update(long t) {
        if (lastTime >= 0) {
            double dt = (t - lastTime) / 1000.0;
            // Position
            Vector3d dPos = new Vector3d(velocity);
            dPos.scale(dt);
            position.add(dPos);
            // Velocity
            acceleration = getForce();
            acceleration.scale(1.0 / mass);
            acceleration.add(environment.getG());
            if (position.z >= environment.getGroundLevel(position) && velocity.z + acceleration.z * dt >= 0.0) {
                // On ground
                acceleration.x = -velocity.x / dt;
                acceleration.y = -velocity.y / dt;
                acceleration.z = -velocity.z / dt;
                position.z = environment.getGroundLevel(position);
                rotationRate.set(0.0, 0.0, 0.0);
            }
            Vector3d dVel = new Vector3d(acceleration);
            dVel.scale(dt);
            velocity.add(dVel);
            // Rotation
            if (rotationRate.length() > 0.0) {
                Matrix3d r = new Matrix3d();
                Vector3d rotationAxis = new Vector3d(rotationRate);
                rotationAxis.normalize();
                r.set(new AxisAngle4d(rotationAxis, rotationRate.length() * dt));
                rotation.mulNormalize(r);
            }
            // Rotation rate
            Vector3d Iw = new Vector3d(rotationRate);
            momentOfInertia.transform(Iw);
            Vector3d angularAcc = new Vector3d();
            angularAcc.cross(rotationRate, Iw);
            angularAcc.negate();
            angularAcc.add(getTorque());
            momentOfInertiaInv.transform(angularAcc);
            angularAcc.scale(dt);
            rotationRate.add(angularAcc);
        }
        lastTime = t;
    }

    @Override
    public Vector3d getPosition() {
        return position;
    }

    @Override
    public Vector3d getVelocity() {
        return velocity;
    }

    @Override
    public Vector3d getAcceleration() {
        return acceleration;
    }

    @Override
    public Matrix3d getRotation() {
        return rotation;
    }

    @Override
    public Vector3d getRotationRate() {
        return rotationRate;
    }

    protected abstract Vector3d getForce();

    protected abstract Vector3d getTorque();
}
