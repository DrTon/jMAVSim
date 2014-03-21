package me.drton.jmavsim;

import me.drton.jmavsim.vehicle.AbstractVehicle;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.common.*;

import javax.vecmath.Vector3d;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MAVLinkHILSystem is MAVLink bridge between AbstractVehicle and autopilot connected via MAVLink.
 * <p/>
 * User: ton Date: 13.02.14 Time: 22:04
 */
public class MAVLinkHILSystem extends MAVLinkSystem {
    private AbstractVehicle vehicle;
    private boolean gotHeartBeat = false;
    private boolean inited = false;
    private long initTime = 0;
    private long initDelay = 1000;
    private long msgIntervalGPS = 200;
    private long msgLastGPS = 0;

    public MAVLinkHILSystem(int sysId, int componentId, AbstractVehicle vehicle) {
        super(sysId, componentId);
        this.vehicle = vehicle;
        msgLastGPS = System.currentTimeMillis() + 10000;
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        super.handleMessage(msg);
        long t = System.currentTimeMillis();
        if (msg instanceof msg_hil_controls) {
            msg_hil_controls msg_hil = (msg_hil_controls) msg;
            List<Double> control = Arrays.asList((double) msg_hil.roll_ailerons, (double) msg_hil.pitch_elevator,
                    (double) msg_hil.yaw_rudder, (double) msg_hil.throttle, (double) msg_hil.aux1,
                    (double) msg_hil.aux2, (double) msg_hil.aux3, (double) msg_hil.aux4);
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
                vehicle.setControl(Collections.<Double>emptyList());
            }
        } else if (msg instanceof msg_statustext) {
            System.out.println("MSG: " + ((msg_statustext) msg).getText());
        }
    }

    @Override
    public void update(long t) {
        super.update(t);
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
        sendMessage(msg_sensor);
        // GPS
        if (t - msgLastGPS > msgIntervalGPS) {
            msgLastGPS = t;
            msg_hil_gps msg_gps = new msg_hil_gps(sysId, componentId);
            msg_gps.time_usec = tu;
            GlobalPosition gps = sensors.getGlobalPosition();
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
            msg_gps.fix_type = gps.fix;
            msg_gps.satellites_visible = 10;
            sendMessage(msg_gps);
        }
    }

    private void initMavLink() {
        // Set HIL mode
        org.mavlink.messages.common.msg_set_mode msg = new org.mavlink.messages.common.msg_set_mode(sysId, componentId);
        msg.base_mode = 32;     // HIL, disarmed
        sendMessage(msg);
    }
}
