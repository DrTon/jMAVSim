package me.drton.jmavsim;

import javax.vecmath.Vector3d;

/**
 * User: ton Date: 28.11.13 Time: 22:40
 */
public class SimpleEnvironment implements Environment {
    private Vector3d magField = new Vector3d(1.0, 0.0, 0.0);
    private Vector3d wind = new Vector3d(0.0, 0.0, 0.0);
    private double groundLevel = 0.0;
    private Vector3d g = new Vector3d(0.0, 0.0, 9.80665);

    @Override
    public Vector3d getG() {
        return g;
    }

    @Override
    public Vector3d getMagField(Vector3d point) {
        return magField;
    }

    public void setMagField(Vector3d magField) {
        this.magField = magField;
    }

    @Override
    public Vector3d getWind(Vector3d point) {
        return wind;
    }

    public void setWind(Vector3d wind) {
        this.wind = wind;
    }

    @Override
    public double getGroundLevel(Vector3d point) {
        return groundLevel;
    }

    public void setGroundLevel(double groundLevel) {
        this.groundLevel = groundLevel;
    }
}
