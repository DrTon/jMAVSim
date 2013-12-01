package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;
import me.drton.jmavsim.vehicle.AbstractMultirotor;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 16:00
 */
public class Quadrotor extends AbstractMultirotor {
    private static final int rotorsNum = 4;
    private Vector3d[] rotors = new Vector3d[rotorsNum];
    private double rotorThrust;
    private double rotorTorque;

    public Quadrotor(Environment environment, String orientation, double armLength, double rotorThrust,
                     double rotorTorque) {
        super(environment);
        rotors[0] = new Vector3d(0.0, armLength, 0.0);
        rotors[1] = new Vector3d(0.0, -armLength, 0.0);
        rotors[2] = new Vector3d(armLength, 0.0, 0.0);
        rotors[3] = new Vector3d(-armLength, 0.0, 0.0);
        if (orientation.equals("x") || orientation.equals("X")) {
            Matrix3d r = new Matrix3d();
            r.rotZ(-Math.PI / 4);
            for (int i = 0; i < rotorsNum; i++) {
                r.transform(rotors[i]);
            }
        } else if (orientation.equals("+")) {
        } else {
            throw new RuntimeException("Unknown quadrotor orientation: " + orientation);
        }
        this.rotorThrust = rotorThrust;
        this.rotorTorque = rotorTorque;
    }

    @Override
    protected int getRotorsNum() {
        return rotorsNum;
    }

    @Override
    protected Vector3d getRotor(int i) {
        return rotors[i];
    }

    @Override
    protected double getRotorThrust(int i) {
        return rotorThrust;
    }

    @Override
    protected double getRotorTorque(int i) {
        return i < 2 ? -rotorTorque : rotorTorque;
    }

    @Override
    protected double[] initControl() {
        double[] ctl = new double[rotorsNum];
        for (int i = 0; i < rotorsNum; i++)
            ctl[i] = 0.0;
        return ctl;
    }
}
