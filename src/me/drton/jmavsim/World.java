package me.drton.jmavsim;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ton Date: 02.02.14 Time: 11:33
 */
public class World {
    private List<WorldObject> objects = new ArrayList<WorldObject>();
    private Environment environment = null;

    public void addObject(WorldObject obj) {
        objects.add(obj);
        if (obj instanceof Environment)
            environment = (Environment) obj;
    }

    public List<WorldObject> getObjects() {
        return objects;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public synchronized void update(long t) {
        for (WorldObject obj : objects)
            obj.update(t);
    }
}
