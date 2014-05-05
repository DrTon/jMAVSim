package me.drton.jmavsim;

import com.sun.j3d.utils.geometry.Sphere;

import java.io.FileNotFoundException;

/**
 * User: ton Date: 01.02.14 Time: 22:12
 */
public class Target extends KinematicObject {
    private GlobalPositionProjector gpsProjector = new GlobalPositionProjector();

    public Target(World world, double size) throws FileNotFoundException {
        super(world);
        Sphere sphere = new Sphere((float) size);
        transformGroup.addChild(sphere);
        gpsProjector.init(world.getGlobalReference());
    }

    public GlobalPositionVelocity getGlobalPosition() {
        LatLonAlt p = gpsProjector.reproject(getPosition());
        GlobalPositionVelocity gps = new GlobalPositionVelocity();
        gps.position = p;
        gps.eph = 1.0;
        gps.epv = 1.0;
        gps.velocity = getVelocity();
        return gps;
    }
}
