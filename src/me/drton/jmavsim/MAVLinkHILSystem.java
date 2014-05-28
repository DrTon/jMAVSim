package me.drton.jmavsim;

import me.drton.jmavlib.processing.DelayLine;
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
    // MAVLinkHILSystem has the same sysID as autopilot, but different componentId
    private int hilComponentId = -1;    // componentId of the autopilot
    private AbstractVehicle vehicle;
    private boolean gotHeartBeat = false;
    private boolean inited = false;
    private long initTime = 0;
    private long initDelay = 1000;
    private long msgIntervalGPS = 200;
    private long msgLastGPS = 0;
    private DelayLine<GlobalPositionVelocity> gpsDelayLine = new DelayLine<GlobalPositionVelocity>();

    /**
     * Create MAVLinkHILSimulator, MAVLink system thet sends simulated sensors to autopilot and passes controls from
     * autopilot to simulator
     *
     * @param sysId       SysId of simulator should be the same as autopilot
     * @param componentId ComponentId of simulator should be different from autopilot
     * @param vehicle
     */
    public MAVLinkHILSystem(int sysId, int componentId, AbstractVehicle vehicle) {
        super(sysId, componentId);
        this.vehicle = vehicle;
    }

    public void setGPSStartTime(long time) {
        msgLastGPS = time;
    }

    public void setGPSDelay(long delay) {
        gpsDelayLine.setDelay(delay);
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        super.handleMessage(msg);
        long t = System.currentTimeMillis();
        if (msg instanceof msg_hil_controls) {
            msg_hil_controls hil_controls = (msg_hil_controls) msg;
            List<Double> control = Arrays.asList((double) hil_controls.roll_ailerons,
                    (double) hil_controls.pitch_elevator, (double) hil_controls.yaw_rudder,
                    (double) hil_controls.throttle, (double) hil_controls.aux1, (double) hil_controls.aux2,
                    (double) hil_controls.aux3, (double) hil_controls.aux4);
            vehicle.setControl(control);
        } else if (msg instanceof msg_heartbeat) {
            msg_heartbeat heartbeat = (msg_heartbeat) msg;
            if (!gotHeartBeat && sysId == heartbeat.sysId) {
                hilComponentId = heartbeat.componentId;
                gotHeartBeat = true;
                initTime = t + initDelay;
            }
            if (!inited && t > initTime) {
                System.out.println("Init MAVLink");
                initMavLink();
                inited = true;
            }
            if ((heartbeat.base_mode & 128) == 0) {
                vehicle.setControl(Collections.<Double>emptyList());
            }
        } else if (msg instanceof msg_statustext) {
            System.out.println("MSG: " + ((msg_statustext) msg).getText());
        }
    }

    @Override
    public void update(long t) {
        // Don't call super.update(), because heartbeats already sent by autopilot
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
            GlobalPositionVelocity p = gpsDelayLine.getOutput(t, sensors.getGlobalPosition());
            if (p != null) {
                msgLastGPS = t;
                msg_hil_gps msg_gps = new msg_hil_gps(sysId, componentId);
                msg_gps.time_usec = tu;
                msg_gps.lat = (long) (p.position.lat * 1e7);
                msg_gps.lon = (long) (p.position.lon * 1e7);
                msg_gps.alt = (long) (p.position.alt * 1e3);
                msg_gps.vn = (int) (p.velocity.x * 100);
                msg_gps.ve = (int) (p.velocity.y * 100);
                msg_gps.vd = (int) (p.velocity.z * 100);
                msg_gps.eph = (int) (p.eph * 100);
                msg_gps.epv = (int) (p.epv * 100);
                msg_gps.vel = (int) (p.getSpeed() * 100);
                msg_gps.cog = (int) (p.getCog() / Math.PI * 18000.0);
                msg_gps.fix_type = p.fix;
                msg_gps.satellites_visible = 10;
                sendMessage(msg_gps);
            }
        }
    }

    private void initMavLink() {
        // Set HIL mode
        msg_set_mode msg = new msg_set_mode(sysId, componentId);
        msg.target_system = sysId;
        msg.base_mode = 32;     // HIL, disarmed
        sendMessage(msg);
    }
}
