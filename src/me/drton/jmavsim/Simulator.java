package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.AbstractMulticopter;
import me.drton.jmavsim.vehicle.Quadcopter;
import me.drton.jmavsim.vehicle.Vehicle;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.common.*;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * User: ton Date: 26.11.13 Time: 12:33
 */
public class Simulator {
    private Environment environment;
    private Vehicle vehicle;
    private Visualizer visualizer;
    private MAVLinkPort mavlinkPort;
    private MAVLinkPort mavlinkPort1;
    private boolean gotHeartBeat = false;
    private boolean inited = false;
    private int sysId = -1;
    private int componentId = -1;
    private int sleepInterval = 8;
    private long nextRun = 0;
    private long msgIntervalGPS = 200;
    private long msgLastGPS = 0;
    private long initTime = 0;
    private long initDelay = 1000;

    public Simulator() throws IOException, InterruptedException {
        // Create environment
        SimpleEnvironment simpleEnvironment = new SimpleEnvironment();
        simpleEnvironment.setMagField(new Vector3d(0.2f, 0.0f, 0.5f));
        //simpleEnvironment.setWind(new Vector3d(0.0, 5.0, 0.0));
        simpleEnvironment.setGroundLevel(0.0f);
        environment = simpleEnvironment;
        // Create vehicle with sensors
        Vector3d gc = new Vector3d(0.0, 0.0, 0.0);  // gravity center
        AbstractMulticopter v = new Quadcopter(environment, "x", 0.33 / 2, 4.0, 0.05, 0.005, gc);
        v.setMass(0.8);
        Matrix3d I = new Matrix3d();
        // Moments of inertia
        I.m00 = 0.005;  // X
        I.m11 = 0.005;  // Y
        I.m22 = 0.009;  // Z
        v.setMomentOfInertia(I);
        SimpleSensors sensors = new SimpleSensors();
        sensors.initGPS(55.753395, 37.625427);
        v.setSensors(sensors);
        v.setDragMove(0.02);
        //v.setDragRotate(0.1);
        vehicle = v;
        // Create visualizer
        visualizer = new Visualizer(environment);
        visualizer.setVehicle(vehicle, "models/3dr_arducopter_quad_x.obj");
        // Create and open port
        gotHeartBeat = false;
        inited = false;
        SerialMAVLinkPort serialMAVLinkPort = new SerialMAVLinkPort();
        serialMAVLinkPort.open("/dev/tty.usbmodem1", 230400, 8, 1, 0);
        UDPMavLinkPort udpMavLinkPort = new UDPMavLinkPort();
        udpMavLinkPort.open(new InetSocketAddress(14555));
        mavlinkPort = serialMAVLinkPort;
        mavlinkPort1 = udpMavLinkPort;
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
        long t = System.currentTimeMillis();
        if (msg instanceof msg_hil_controls) {
            msg_hil_controls msg_hil = (msg_hil_controls) msg;
            double[] control = new double[]{
                    msg_hil.roll_ailerons, msg_hil.pitch_elevator, msg_hil.yaw_rudder, msg_hil.throttle, msg_hil.aux1,
                    msg_hil.aux2, msg_hil.aux3, msg_hil.aux4};
            vehicle.setControl(control);
        } else if (msg instanceof msg_heartbeat) {
            msg_heartbeat msg_heartbeat = (msg_heartbeat) msg;
            if (!gotHeartBeat) {
                sysId = msg_heartbeat.sysId;
                componentId = msg_heartbeat.componentId;
                gotHeartBeat = true;
                initTime = t + initDelay;
            }
            if (!inited && t > initTime) {
                System.out.println("Init MAVLink");
                initMavLink();
                inited = true;
            }
            if ((msg_heartbeat.base_mode & 128) == 0) {
                double[] control = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                vehicle.setControl(control);
            }
        } else if (msg instanceof msg_statustext) {
            System.out.println("MSG: " + ((msg_statustext) msg).getText());
        }
    }

    private void sendMavLinkMessages() throws IOException {
        long t = System.currentTimeMillis();
        long tu = t * 1000;
        Sensors sensors = vehicle.getSensors();
        // Sensors
        msg_hil_sensor msg_sensor = new msg_hil_sensor(sysId, componentId);
        msg_sensor.time_usec = tu;
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
        // GPS
        if (t - msgLastGPS > msgIntervalGPS) {
            msgLastGPS = t;
            msg_hil_gps msg_gps = new msg_hil_gps(sysId, componentId);
            msg_gps.time_usec = tu;
            GPSPosition gps = sensors.getGPS();
            msg_gps.lat = (long) (gps.lat * 1e7);
            msg_gps.lon = (long) (gps.lon * 1e7);
            msg_gps.alt = (long) (gps.alt * 1e3);
            msg_gps.vn = (int) (gps.vn * 100);
            msg_gps.ve = (int) (gps.ve * 100);
            msg_gps.vd = (int) (gps.vd * 100);
            msg_gps.eph = (int) (gps.eph * 100);
            msg_gps.epv = (int) (gps.epv * 100);
            msg_gps.vel = (int) (gps.getSpeed() * 100);
            msg_gps.cog = (int) (gps.getCog() / Math.PI * 18000.0);
            msg_gps.fix_type = 3;
            msg_gps.satellites_visible = 10;
            mavlinkPort.sendMessage(msg_gps);
        }
    }

    public void run() throws IOException, InterruptedException {
        nextRun = System.currentTimeMillis() + sleepInterval;
        while (true) {
            while (System.currentTimeMillis() < nextRun - sleepInterval * 3 / 4) {
                MAVLinkMessage msg = mavlinkPort.getNextMessage();
                if (msg == null)
                    break;
                handleMavLinkMessage(msg);
                mavlinkPort1.sendMessage(msg);
            }
            while (System.currentTimeMillis() < nextRun - sleepInterval * 3 / 4) {
                MAVLinkMessage msg = mavlinkPort1.getNextMessage();
                if (msg == null)
                    break;
                mavlinkPort.sendMessage(msg);
            }
            long t = System.currentTimeMillis();
            vehicle.update(t);
            visualizer.update(t);
            if (mavlinkPort.isOpened() && inited)
                sendMavLinkMessages();
            long timeLeft = Math.max(sleepInterval / 4, nextRun - System.currentTimeMillis());
            nextRun = Math.max(t + sleepInterval / 4, nextRun + sleepInterval);
            Thread.sleep(timeLeft);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new Simulator();
    }
}
