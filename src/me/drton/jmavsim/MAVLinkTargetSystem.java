package me.drton.jmavsim;

import org.mavlink.messages.common.msg_global_position_int;

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
            msg_global_position_int msg_target = new msg_global_position_int(2, componentId);
            GPSPosition p = target.getGlobalPosition();
            msg_target.time_boot_ms = t * 1000;
            msg_target.lat = (long) (p.position.lat * 1e7);
            msg_target.lon = (long) (p.position.lon * 1e7);
            msg_target.alt = (long) (p.position.alt * 1e3);
            msg_target.vx = (int) (p.velocity.x * 100);
            msg_target.vy = (int) (p.velocity.y * 100);
            msg_target.vz = (int) (p.velocity.z * 100);
            sendMessage(msg_target);
        }
    }
}
