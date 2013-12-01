package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 29.11.13 Time: 16:59
 */
public abstract class AbstractMultirotor extends AbstractVehicle {
    private double dragMove = 0.0;
    private double dragRotate = 0.0;

    public AbstractMultirotor(Environment environment) {
        super(environment);
        control = initControl();
    }

    /**
     * Get number of rotors.
     *
     * @return number of rotors
     */
    protected abstract int getRotorsNum();

    /**
     * Get rotor coordinates.
     *
     * @param i rotor number
     * @return rotor radius-vector from GC
     */
    protected abstract Vector3d getRotor(int i);

    /**
     * Get rotor thrust at full throttle.
     *
     * @param i rotor number
     * @return thrust in newtons
     */
    protected abstract double getRotorThrust(int i);

    /**
     * Get rotor torque at full throttle
     *
     * @param i rotor number
     * @return rotor torque, positive for CW and negative for CCW rotating propellers (top view)
     */
    protected abstract double getRotorTorque(int i);

    public void setDragMove(double dragMove) {
        this.dragMove = dragMove;
    }

    public void setDragRotate(double dragRotate) {
        this.dragRotate = dragRotate;
    }

    @Override
    protected Vector3d getForce() {
        int n = getRotorsNum();
        Vector3d f = new Vector3d();
        for (int i = 0; i < n; i++) {
            f.z -= control[i] * getRotorThrust(i);
        }
        rotation.transform(f);
        Vector3d airSpeed = new Vector3d(getVelocity());
        airSpeed.scale(-1.0);
        airSpeed.add(environment.getWind(position));
        f.add(getAirFlowForce(airSpeed));
        return f;
    }

    @Override
    protected Vector3d getTorque() {
        int n = getRotorsNum();
        Vector3d torque = new Vector3d();
        Vector3d m = new Vector3d();
        Vector3d t = new Vector3d();
        for (int i = 0; i < n; i++) {
            // Roll / pitch
            t.z = -control[i] * getRotorThrust(i);
            m.cross(getRotor(i), t);
            // Yaw
            m.z -= control[i] * getRotorTorque(i);
            torque.add(m);
        }
        Vector3d airRotationRate = new Vector3d(rotationRate);
        airRotationRate.scale(-1.0);
        torque.add(getAirFlowTorque(airRotationRate));
        return torque;
    }

    protected Vector3d getAirFlowForce(Vector3d airSpeed) {
        Vector3d f = new Vector3d(airSpeed);
        f.scale(f.length() * dragMove);
        return f;
    }

    protected Vector3d getAirFlowTorque(Vector3d airRotationRate) {
        Vector3d f = new Vector3d(airRotationRate);
        f.scale(f.length() * dragRotate);
        return f;
    }
}
