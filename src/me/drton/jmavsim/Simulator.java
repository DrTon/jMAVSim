package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.AbstractMultirotor;
import me.drton.jmavsim.vehicle.Quadrotor;
import me.drton.jmavsim.vehicle.Vehicle;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.common.*;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.io.IOException;

/**
 * User: ton Date: 26.11.13 Time: 12:33
 */
public class Simulator {
    private Environment environment;
    private Vehicle vehicle;
    private Visualizer visualizer;
    private MAVLinkPort mavlinkPort;
    private boolean gotHeartBeat = false;
    private int sysId = -1;
    private int componentId = -1;
    private int sleepInterval = 5;

    public Simulator() throws IOException {
        // Create environment
        environment = new SimpleEnvironment();
        // Create vehicle with sensors
        AbstractMultirotor v = new Quadrotor(environment, "x", 0.55 / 2, 5.0, 1.0);
        v.setMass(1.0);
        Matrix3d I = new Matrix3d();
        I.rotZ(0.0);
        I.setScale(0.01);
        v.setMomentOfInertia(I);
        v.setSensors(new SimpleSensors());
        v.setDragMove(0.1);
        //v.setDragRotate(0.1);
        vehicle = v;
        // Create visualizer
        visualizer = new Visualizer(environment);
        visualizer.setVehicle(vehicle, "models/3dr_arducopter_quad_x.obj");
        // Create and open port
        gotHeartBeat = false;
        mavlinkPort = new MAVLinkPort();
        mavlinkPort.open("/dev/tty.usbmodem1", 230400, 8, 1, 0);
        // Run
        try {
            run();
        } catch (InterruptedException e) {
            System.out.println("Exit");
        }
        // Close port
        mavlinkPort.close();
    }

    private void initMavLink() throws IOException {
        // Set HIL mode
        org.mavlink.messages.common.msg_set_mode msg = new org.mavlink.messages.common.msg_set_mode(sysId, componentId);
        msg.base_mode = 32;     // HIL, disarmed
        mavlinkPort.sendMessage(msg);
    }

    private void handleMavLinkMessage(MAVLinkMessage msg) throws IOException {
        //System.out.println("MSG: " + msg);
        if (msg instanceof msg_hil_controls) {
            msg_hil_controls msg_hil = (msg_hil_controls) msg;
            double[] control = new double[]{
                    msg_hil.roll_ailerons, msg_hil.pitch_elevator, msg_hil.yaw_rudder, msg_hil.throttle};
            vehicle.setControl(control);
        } else if (msg instanceof msg_heartbeat) {
            if (!gotHeartBeat) {
                msg_heartbeat msg_heartbeat = (msg_heartbeat) msg;
                sysId = msg_heartbeat.sysId;
                componentId = msg_heartbeat.componentId;
                gotHeartBeat = true;
                initMavLink();
            }
        } else if (msg instanceof msg_statustext) {
            System.out.println("MSG: " + ((msg_statustext) msg).getText());
        } else if (msg instanceof msg_local_position_ned) {
            msg_local_position_ned lpos = (msg_local_position_ned) msg;
            System.out.println("LPOS: " + lpos);
        }
    }

    private void sendMavLinkMessages() throws IOException {
        // Sensors
        msg_hil_sensor msg_sensor = new msg_hil_sensor(sysId, componentId);
        msg_sensor.time_usec = System.currentTimeMillis() * 1000;
        Sensors sensors = vehicle.getSensors();
        Vector3d acc = sensors.getAcc();
        msg_sensor.xacc = (float) acc.x;
        msg_sensor.yacc = (float) acc.y;
        msg_sensor.zacc = (float) acc.z;
        Vector3d gyro = sensors.getGyro();
        msg_sensor.xgyro = (float) gyro.x;
        msg_sensor.ygyro = (float) gyro.y;
        msg_sensor.zgyro = (float) gyro.z;
        Vector3d mag = sensors.getMag();
        msg_sensor.xmag = (float) mag.x;
        msg_sensor.ymag = (float) mag.y;
        msg_sensor.zmag = (float) mag.z;
        msg_sensor.pressure_alt = (float) sensors.getPressureAlt();
        mavlinkPort.sendMessage(msg_sensor);
    }

    public void run() throws IOException, InterruptedException {
        while (true) {
            while (mavlinkPort.hasNextMessage()) {
                MAVLinkMessage msg = mavlinkPort.getNextMessage();
                if (msg == null)
                    break;
                handleMavLinkMessage(msg);
            }
            long t = System.currentTimeMillis();
            vehicle.update(t);
            visualizer.update(t);
            if (mavlinkPort.isOpened() && gotHeartBeat)
                sendMavLinkMessages();
            Thread.sleep(sleepInterval);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new Simulator();
    }
}
