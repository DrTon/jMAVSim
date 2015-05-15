package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkSchema;
import me.drton.jmavlib.mavlink.MAVLinkStream;
import me.drton.jmavlib.mavlink.MAVLinkMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public class UDPMavLinkPort extends MAVLinkPort {

    public static final String PORT_ADDRESS = "PORT_ADDRESS";

    private MAVLinkSchema schema;
    private DatagramChannel channel = null;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private MAVLinkStream stream;
    private SocketAddress sendAddress;
    private int portAddress;
    private boolean isClient;

    public UDPMavLinkPort(MAVLinkSchema schema) {
        super(schema);
        this.schema = schema;
        rxBuffer.flip();
    }

    public void setup(int portAddress, boolean client) {
        // If isClient, the portAddress is the destination port, else it's the source port. 
        this.portAddress = portAddress;
        this.isClient = client;
    }

    public void open() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        stream = new MAVLinkStream(schema);
        if (isClient) {
            channel.socket().bind(new InetSocketAddress(0)); // default source port 0 for clients
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            // Must put at least one byte of data in the buffer;
            // it doesn't matter what it is.
            buffer.put((byte) 65); // ASCII "A"
            buffer.flip();
	    // Currently assumes localhost
            SocketAddress server = new InetSocketAddress("127.0.0.1", portAddress);
            channel.send(buffer, server);
            buffer.clear();
        }
	else {
	  channel.socket().bind(new InetSocketAddress(portAddress));
	}
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public boolean isOpened() {
        return channel != null && channel.isOpen();
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        if (isOpened() && sendAddress != null) {
            try {
                channel.send(stream.write(msg), sendAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(long t) {
        MAVLinkMessage msg;
        while (isOpened()) {
            try {
                rxBuffer.compact();
                SocketAddress addr = channel.receive(rxBuffer);
                rxBuffer.flip();
                msg = stream.read(rxBuffer);
                if (msg == null) {
                    break;
                }
                if (addr != null) {
                    sendAddress = addr;
                }
                System.out.println("Message: " + msg.getMsgName() + ", " + msg.getMsgType() + ", " + portAddress);
                sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void setSendAddress(SocketAddress sendAddress) {
        this.sendAddress = sendAddress;
    }
}
