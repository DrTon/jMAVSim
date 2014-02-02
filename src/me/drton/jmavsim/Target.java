package me.drton.jmavsim;

import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;

/**
 * User: ton Date: 01.02.14 Time: 22:12
 */
public class Target extends VisualObject {
    protected Vector3d position = new Vector3d();
    protected Vector3d velocity = new Vector3d();
    protected Vector3d acceleration = new Vector3d();
    protected long lastTime = -1;
    protected long startTime = -1;
    public double dragMove = 0.02;
    public double mass = 90.0;
    private GlobalPositionProjector gpsProjector = new GlobalPositionProjector();

    public Target(World world) throws FileNotFoundException {
        super(world);
        Sphere sphere = new Sphere(1.0f);
        transform = new Transform3D();
        transformGroup = new TransformGroup(transform);
        transformGroup.addChild(sphere);
        branchGroup.addChild(transformGroup);
    }

    public void initGPS(double lat, double lon) {
        gpsProjector.init(lat, lon);
    }

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
            Vector3d dVel = new Vector3d(acceleration);
            dVel.scale(dt);
            velocity.add(dVel);
            System.out.println(velocity);
        } else {
            startTime = t;
        }
        lastTime = t;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    protected Vector3d getForce() {
        if (lastTime - startTime < 10000)
            return new Vector3d(0, 0, 0);
        Vector3d f = new Vector3d(velocity);
        f.scale(-f.length() * dragMove);
        f.add(new Vector3d(Math.exp(-position.length() / 700.0) * mass * 9.81 * 0.25, 0.0, 0.0));
        return f;
    }

    @Override
    protected Vector3d getTorque() {
        return new Vector3d(0.0, 0.0, 0.0);
    }

    public GPSPosition getGPS() {
        double[] latlon = gpsProjector.reproject(getPosition().x, getPosition().y);
        GPSPosition gps = new GPSPosition();
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
