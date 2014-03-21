package me.drton.jmavsim;

import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;

/**
 * User: ton Date: 21.03.14 Time: 13:44
 */
public class MavlinkTest {
    public static void main(String[] args) throws InterruptedException, IOException {
        World world = new World();
        MAVLinkConnection connection = new MAVLinkConnection(world);
        SerialMAVLinkPort port = new SerialMAVLinkPort();
        connection.addNode(port);
        MAVLinkNode node = new MAVLinkNode() {
            @Override
            public void handleMessage(MAVLinkMessage msg) {
                System.out.println(msg);
            }
        };
        connection.addNode(node);
        port.open("/dev/tty.usbserial-DC008SB7", 57600, 8, 1, 0);
        while (true) {
            port.update(System.currentTimeMillis());
        }
    }
}
