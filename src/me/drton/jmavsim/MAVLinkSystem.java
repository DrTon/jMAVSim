package me.drton.jmavsim;

import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.common.msg_heartbeat;

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

    public MAVLinkSystem(int sysId, int componentId) {
        this.sysId = sysId;
        this.componentId = componentId;
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
    }

    @Override
    public void update(long t) {
        if (t - heartbeatLast >= heartbeatInterval) {
            msg_heartbeat msg = new msg_heartbeat(sysId, componentId);
            sendMessage(msg);
        }
    }
}
