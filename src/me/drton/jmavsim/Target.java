package me.drton.jmavsim;

import com.sun.j3d.utils.geometry.Sphere;

import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;

/**
 * User: ton Date: 01.02.14 Time: 22:12
 */
public class Target extends VisualObject {
    protected long startTime = -1;
    public double dragMove = 0.2;
    private GlobalPositionProjector gpsProjector = new GlobalPositionProjector();

    public Target(World world, double size) throws FileNotFoundException {
        super(world);
        Sphere sphere = new Sphere((float) size);
        transformGroup.addChild(sphere);
    }

    public void initGPS(double lat, double lon) {
        gpsProjector.init(lat, lon);
    }

    @Override
    protected Vector3d getForce() {
        if (startTime < 0)
            startTime = lastTime;
        Vector3d f = new Vector3d(velocity);
        f.scale(-f.length() * dragMove);
        Vector3d mg = new Vector3d(getWorld().getEnvironment().getG());
        mg.scale(-mass);
        f.add(mg);
        if (lastTime - startTime > 60000)
            f.add(new Vector3d(0.0, Math.exp(-position.length() / 700.0) * mass * 9.81 * 0.025, 0.0));
        return f;
    }

    @Override
    protected Vector3d getTorque() {
        return new Vector3d(0.0, 0.0, 0.0);
    }

    public GlobalPosition getGlobalPosition() {
        double[] latlon = gpsProjector.reproject(getPosition().x, getPosition().y);
        GlobalPosition gps = new GlobalPosition();
        gps.lat = latlon[0];
        gps.lon = latlon[1];
        gps.alt = -getPosition().z;
        gps.eph = 1.0;
        gps.epv = 1.0;
        gps.vn = getVelocity().x;
        gps.ve = getVelocity().y;
        gps.vd = getVelocity().z;
        return gps;
    }
}
