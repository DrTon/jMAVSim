package me.drton.jmavsim.vehicle;

import me.drton.jmavsim.DynamicObject;
import me.drton.jmavsim.Sensors;
import me.drton.jmavsim.World;

import javax.vecmath.Vector3d;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract vehicle class, should be used for creating vehicle of any type.
 * Child class should use member 'control' as control input for actuators.
 * 'update()' method of AbstractVehicle must be called from child class implementation if overridden.
 */
public abstract class AbstractVehicle extends DynamicObject {
    protected List<Double> control = Collections.emptyList();
    private Sensors sensors = null;

    public AbstractVehicle(World world, String modelName) throws FileNotFoundException {
        super(world);
        modelFromFile(modelName);
        position.set(0.0, 0.0, world.getEnvironment().getGroundLevel(new Vector3d(0.0, 0.0, 0.0)));
    }

    public void setControl(List<Double> control) {
        this.control = new ArrayList<Double>(control);
    }

    public List<Double> getControl() {
        return control;
    }

    /**
     * Set sensors object for the vehicle.
     *
     * @param sensors
     */
    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
        sensors.setObject(this);
    }

    public Sensors getSensors() {
        return sensors;
    }

    @Override
    public void update(long t) {
        super.update(t);
        if (sensors != null) {
            sensors.update(t);
        }
    }
}
