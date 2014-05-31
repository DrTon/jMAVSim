package me.drton.jmavsim;

import org.mavlink.messages.px4.msg_global_position_int;
import org.mavlink.messages.px4.msg_global_position_time;

/**
 * User: ton Date: 13.02.14 Time: 22:51
 */
public class MAVLinkTargetSystem extends MAVLinkSystem {
    private Target target;
    private long msgIntervalPosition = 200;
    private long msgLastPosition = 0;

    public MAVLinkTargetSystem(int sysId, int componentId, Target target) {
        super(sysId, componentId);
        this.target = target;
    }

    @Override
    public void update(long t) {
        super.update(t);
        if (t - msgLastPosition > msgIntervalPosition) {
            msgLastPosition = t;

            GPSPosition p = target.getGlobalPosition();

            msg_global_position_int msg_target = new msg_global_position_int(2, componentId);
            msg_target.time_boot_ms = t * 1000;
            msg_target.lat = (long) (p.position.lat * 1e7);
            msg_target.lon = (long) (p.position.lon * 1e7);
            msg_target.alt = (long) (p.position.alt * 1e3);
            msg_target.vx = (int) (p.velocity.x * 100);
            msg_target.vy = (int) (p.velocity.y * 100);
            msg_target.vz = (int) (p.velocity.z * 100);
            sendMessage(msg_target);

            msg_global_position_time msg_target_time = new msg_global_position_time(2, componentId);
            msg_target_time.time = t * 1000;
            msg_target_time.lat = (long) (p.position.lat * 1e7);
            msg_target_time.lon = (long) (p.position.lon * 1e7);
            msg_target_time.alt = (float) (p.position.alt);
            msg_target_time.vx = (float) p.velocity.x;
            msg_target_time.vy = (float) p.velocity.y;
            msg_target_time.vz = (float) p.velocity.z;
            sendMessage(msg_target_time);
        }
    }
}
