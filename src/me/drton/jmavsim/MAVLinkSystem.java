package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkMessage;
import me.drton.jmavlib.mavlink.MAVLinkSchema;

/**
 * MAVLinkSystem represents generic MAVLink system with SysID and ComponentID that can handle and send messages.
 * <p/>
 * User: ton Date: 13.02.14 Time: 20:32
 */
public class MAVLinkSystem extends MAVLinkNode {
    public int sysId;
    public int componentId;
    private long heartbeatInterval = 1000;
    private long heartbeatLast = 0;

    public MAVLinkSystem(MAVLinkSchema schema, int sysId, int componentId) {
        super(schema);
        this.sysId = sysId;
        this.componentId = componentId;
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
    }

    @Override
    public void update(long t) {
        if (t - heartbeatLast >= heartbeatInterval) {
            heartbeatLast = t;
            MAVLinkMessage msg = new MAVLinkMessage(schema, "HEARTBEAT", sysId, componentId);
            msg.set("mavlink_version", 3);
            sendMessage(msg);
        }
    }
}
