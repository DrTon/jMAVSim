package me.drton.jmavsim;

/**
 * User: ton Date: 15.12.13 Time: 21:16
 */
public class Rotor {
    private double rc = 1.0;
    private double fullThrust = 1.0;
    private double fullTorque = 1.0;
    private double w = 0.0;
    private long lastTime = -1;
    private double control = 0.0;

    public void update(long t) {
        if (lastTime >= 0) {
            double dt = (t - lastTime) / 1000.0;
            w += (control - w) * (1.0 - Math.exp(-dt * rc));
        }
        lastTime = t;
    }

    public void setControl(double control) {
        this.control = control;
    }

    public void setFullThrust(double fullThrust) {
        this.fullThrust = fullThrust;
    }

    public void setFullTorque(double fullTorque) {
        this.fullTorque = fullTorque;
    }

    public void setTimeConstant(double timeConstant) {
        this.rc = 1.0 / timeConstant / 2.0 / Math.PI;
    }

    public double getThrust() {
        return w * fullThrust;
    }

    public double getTorque() {
        return control * fullTorque;
    }
}
