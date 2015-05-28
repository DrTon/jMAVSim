package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Rotor;
import me.drton.jmavsim.World;

import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;

/**
 * Abstract multicopter class. Does all necessary calculations for multirotor with any placement of rotors.
 * Only rotors on one plane supported now.
 */
public abstract class AbstractMulticopter extends AbstractVehicle {
    private double dragMove = 0.0;
    private double dragRotate = 0.0;
    protected Rotor[] rotors;

    public AbstractMulticopter(World world, String modelName) throws FileNotFoundException {
        super(world, modelName);
        rotors = new Rotor[getRotorsNum()];
        for (int i = 0; i < getRotorsNum(); i++) {
            rotors[i] = new Rotor();
        }
    }

    /**
     * Get number of rotors.
     *
     * @return number of rotors
     */
    protected abstract int getRotorsNum();

    /**
     * Get rotor position relative to gravity center of vehicle.
     *
     * @param i rotor number
     * @return rotor radius-vector from GC
     */
    protected abstract Vector3d getRotorPosition(int i);

    public void setDragMove(double dragMove) {
        this.dragMove = dragMove;
    }

    public void setDragRotate(double dragRotate) {
        this.dragRotate = dragRotate;
    }

    @Override
    public void update(long t) {
        for (Rotor rotor : rotors) {
            rotor.update(t);
        }
        super.update(t);
        for (int i = 0; i < rotors.length; i++) {
            double c = control.size() > i ? control.get(i) : 0.0;
            rotors[i].setControl(c);
        }
    }

    @Override
    protected Vector3d getForce() {
        int n = getRotorsNum();
        Vector3d f = new Vector3d();
        for (int i = 0; i < n; i++) {
            f.z -= rotors[i].getThrust();
        }
        rotation.transform(f);
        Vector3d airSpeed = new Vector3d(getVelocity());
        airSpeed.scale(-1.0);
        airSpeed.add(getWorld().getEnvironment().getWind(position));
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
            t.z = -rotors[i].getThrust();
            m.cross(getRotorPosition(i), t);
            // Yaw
            m.z -= rotors[i].getTorque();
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
