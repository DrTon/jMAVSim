package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkMessage;
import me.drton.jmavlib.mavlink.MAVLinkSchema;

/**
 * User: ton Date: 13.02.14 Time: 22:51
 */
public class MAVLinkTargetSystem extends MAVLinkSystem {
    private Target target;
    private long msgIntervalPosition = 200;
    private long msgLastPosition = 0;

    public MAVLinkTargetSystem(MAVLinkSchema schema, int sysId, int componentId, Target target) {
        super(schema, sysId, componentId);
        this.target = target;
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        super.handleMessage(msg);
        if ("PARAM_REQUEST_LIST".equals(msg.getMsgName())) {
            int target_system = msg.getInt("target_system");
            int target_component = msg.getInt("target_component");
            if (target_system == sysId && (target_component == componentId || target_component == 0)) {
                MAVLinkMessage reply = new MAVLinkMessage(schema, "PARAM_VALUE", sysId, componentId);
                reply.set("param_count", 1);
                reply.set("param_id", "DUMMY");
                reply.set("param_type", 9);
                sendMessage(reply);
            }
        }
    }

    @Override
    public void update(long t) {
        super.update(t);
        if (t - msgLastPosition > msgIntervalPosition) {
            msgLastPosition = t;
            MAVLinkMessage msg_target = new MAVLinkMessage(schema, "GLOBAL_POSITION_INT", sysId, componentId);
            GNSSReport p = target.getGlobalPosition();
            msg_target.set("time_boot_ms", t * 1000);
            msg_target.set("lat", (long) (p.position.lat * 1e7));
            msg_target.set("lon", (long) (p.position.lon * 1e7));
            msg_target.set("alt", (long) (p.position.alt * 1e3));
            msg_target.set("vx", (int) (p.velocity.x * 100));
            msg_target.set("vy", (int) (p.velocity.y * 100));
            msg_target.set("vz", (int) (p.velocity.z * 100));
            sendMessage(msg_target);
        }
    }
}
