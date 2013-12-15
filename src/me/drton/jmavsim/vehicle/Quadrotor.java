package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;
import me.drton.jmavsim.Rotor;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/**
 * User: ton Date: 26.11.13 Time: 16:00
 */
public class Quadrotor extends AbstractMultirotor {
    private static final int rotorsNum = 4;
    private Vector3d[] rotorPositions = new Vector3d[rotorsNum];

    public Quadrotor(Environment environment, String orientation, double armLength, double rotorThrust,
                     double rotorTorque, double rotorTimeConst) {
        super(environment);
        rotorPositions[0] = new Vector3d(0.0, armLength, 0.0);
        rotorPositions[1] = new Vector3d(0.0, -armLength, 0.0);
        rotorPositions[2] = new Vector3d(armLength, 0.0, 0.0);
        rotorPositions[3] = new Vector3d(-armLength, 0.0, 0.0);
        if (orientation.equals("x") || orientation.equals("X")) {
            Matrix3d r = new Matrix3d();
            r.rotZ(-Math.PI / 4);
            for (int i = 0; i < rotorsNum; i++) {
                r.transform(rotorPositions[i]);
            }
        } else if (orientation.equals("+")) {
        } else {
            throw new RuntimeException("Unknown quadrotor orientation: " + orientation);
        }
        for (int i = 0; i < rotors.length; i++) {
            Rotor rotor = rotors[i];
            rotor.setFullThrust(rotorThrust);
            rotor.setFullTorque(i < 2 ? -rotorTorque : rotorTorque);
            rotor.setTimeConstant(rotorTimeConst);
        }
    }

    @Override
    protected int getRotorsNum() {
        return rotorsNum;
    }

    @Override
    protected Vector3d getRotorPosition(int i) {
        return rotorPositions[i];
    }
}
