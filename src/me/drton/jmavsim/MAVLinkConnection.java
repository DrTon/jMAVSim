package me.drton.jmavsim;

import org.mavlink.messages.MAVLinkMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ton Date: 13.02.14 Time: 21:50
 */
public class MAVLinkConnection extends WorldObject {
    private List<MAVLinkNode> nodes = new ArrayList<MAVLinkNode>();

    public MAVLinkConnection(World world) {
        super(world);
    }

    public void addNode(MAVLinkNode node) {
        nodes.add(node);
        node.addConnection(this);
    }

    public void sendMessage(MAVLinkNode sender, MAVLinkMessage msg) {
        for (MAVLinkNode node : nodes) {
            if (node != sender)
                node.handleMessage(msg);
        }
    }

    @Override
    public void update(long t) {
        for (MAVLinkNode node : nodes) {
            node.update(t);
        }
    }
}
