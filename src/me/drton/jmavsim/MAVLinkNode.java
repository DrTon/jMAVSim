package me.drton.jmavsim;

import org.mavlink.messages.MAVLinkMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MAVLinkNode is generic object that can handle and send MAVLink messages, but may have no own ID,
 * i.e. it can be e.g. bridge between physical port and virtual MAVLinkConnection.
 *
 * User: ton Date: 13.02.14 Time: 21:51
 */
public abstract class MAVLinkNode {
    private List<MAVLinkConnection> connections = new ArrayList<MAVLinkConnection>();

    public void addConnection(MAVLinkConnection connection) {
        connections.add(connection);
    }

    protected void sendMessage(MAVLinkMessage msg) {
        for (MAVLinkConnection connection : connections) {
            connection.sendMessage(this, msg);
        }
    }

    public abstract void handleMessage(MAVLinkMessage msg);

    public abstract void update(long t);
}
