package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.Environment;
import me.drton.jmavsim.VisualObject;
import me.drton.jmavsim.World;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: ton Date: 26.11.13 Time: 16:00
 */
public abstract class AbstractVehicle extends VisualObject {
    protected List<Double> control = Collections.emptyList();

    public AbstractVehicle(World world, String modelName) throws FileNotFoundException {
        super(world);
        modelFromFile(modelName);
        position.set(0.0, 0.0, world.getEnvironment().getGroundLevel(new Vector3d(0.0, 0.0, 0.0)));
    }

    public void setControl(List<Double> control) {
        control = new ArrayList<Double>(control);
    }
}
