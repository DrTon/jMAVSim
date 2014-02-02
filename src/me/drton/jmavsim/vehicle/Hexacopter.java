package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Rotor;
import me.drton.jmavsim.World;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;

/**
 * User: ton Date: 18.01.14 Time: 21:01
 */
public class Hexacopter extends AbstractMulticopter {
    private static final int rotorsNum = 6;
    private Vector3d[] rotorPositions = new Vector3d[rotorsNum];

    public Hexacopter(World world, String modelName, String orientation, double armLength, double rotorThrust,
                      double rotorTorque, double rotorTimeConst) throws FileNotFoundException {
        super(world, modelName);
        rotorPositions[0] = new Vector3d(armLength, 0.0, 0.0);
        rotorPositions[1] = new Vector3d(-armLength, 0.0, 0.0);
        rotorPositions[2] = new Vector3d(-armLength * Math.cos(Math.PI / 3), -armLength * Math.sin(Math.PI / 3), 0.0);
        rotorPositions[3] = new Vector3d(armLength * Math.cos(Math.PI / 3), armLength * Math.sin(Math.PI / 3), 0.0);
        rotorPositions[4] = new Vector3d(armLength * Math.cos(Math.PI / 3), -armLength * Math.sin(Math.PI / 3), 0.0);
        rotorPositions[5] = new Vector3d(-armLength * Math.cos(Math.PI / 3), armLength * Math.sin(Math.PI / 3), 0.0);
        if (orientation.equals("x") || orientation.equals("X")) {
            Matrix3d r = new Matrix3d();
            r.rotZ(Math.PI / 2);
            for (int i = 0; i < rotorsNum; i++) {
                r.transform(rotorPositions[i]);
            }
        } else if (orientation.equals("+")) {
        } else {
            throw new RuntimeException("Unknown orientation: " + orientation);
        }
        for (int i = 0; i < rotors.length; i++) {
            Rotor rotor = rotors[i];
            rotor.setFullThrust(rotorThrust);
            rotor.setFullTorque((i == 1 || i == 3 || i == 4) ? -rotorTorque : rotorTorque);
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
