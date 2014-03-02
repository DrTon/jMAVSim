package me.drton.jmavsim;

import org.mavlink.messages.px4.*;

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

            GlobalPosition target_pos = target.getGlobalPosition();

            msg_global_position_int msg_target_int = new msg_global_position_int(2, componentId);
            msg_target_int.time_boot_ms = t * 1000;
            msg_target_int.lat = (long) (target_pos.lat * 1e7);
            msg_target_int.lon = (long) (target_pos.lon * 1e7);
            msg_target_int.alt = (long) (target_pos.alt * 1e3);
            msg_target_int.vx = (int) (target_pos.vn * 100);
            msg_target_int.vy = (int) (target_pos.ve * 100);
            msg_target_int.vz = (int) (target_pos.vd * 100);
            sendMessage(msg_target_int);

            msg_global_position_time msg_target = new msg_global_position_time(2, componentId);
            msg_target.time = t * 1000;
            msg_target.lat = (long) (target_pos.lat * 1e7);
            msg_target.lon = (long) (target_pos.lon * 1e7);
            msg_target.alt = (float) target_pos.alt;
            msg_target.vx = (float) target_pos.vn;
            msg_target.vy = (float) target_pos.ve;
            msg_target.vz = (float) target_pos.vd;
            sendMessage(msg_target);
        }
    }
}
