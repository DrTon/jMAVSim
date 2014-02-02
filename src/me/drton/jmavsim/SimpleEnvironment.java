package me.drton.jmavsim;

import javax.vecmath.Vector3d;
import java.util.Random;

/**
 * User: ton Date: 28.11.13 Time: 22:40
 */
public class SimpleEnvironment extends Environment {
    private Vector3d magField = new Vector3d(1.0, 0.0, 0.0);
    private Vector3d wind = new Vector3d(0.0, 0.0, 0.0);
    private double groundLevel = 0.0;
    private Vector3d g = new Vector3d(0.0, 0.0, 9.80665);
    private double windDeviation = 20.0;
    private double windT = 2.0;
    private Vector3d windCurrent = new Vector3d(0.0, 0.0, 0.0);
    private Random random = new Random();
    private long lastTime = 0;

    public SimpleEnvironment(World world) {
        super(world);
    }

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
        return windCurrent;
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

    public void update(long t) {
        double dt = lastTime == 0 ? 0.0 : (t - lastTime) / 1000.0;
        lastTime = t;
        Vector3d r = new Vector3d(random.nextGaussian() * windDeviation, random.nextGaussian() * windDeviation, 0.0);
        Vector3d dev = new Vector3d(wind);
        dev.sub(windCurrent);
        dev.scale(1.0 / windT);
        r.add(dev);
        r.scale(dt);
        windCurrent.add(r);
    }
}
