package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkSchema;
import me.drton.jmavlib.mavlink.MAVLinkStream;
import me.drton.jmavlib.mavlink.MAVLinkMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public class UDPMavLinkPort extends MAVLinkPort {
    private MAVLinkSchema schema;
    private DatagramChannel channel = null;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private SocketAddress sendAddress;
    private SocketAddress bindPort;
    private SocketAddress peerPort;
    private int portAddress;
    private boolean isClient;
    private MAVLinkStream stream;
    private boolean debug = false;

    public UDPMavLinkPort(MAVLinkSchema schema) {
        super(schema);
        this.schema = schema;
        rxBuffer.flip();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setup(int bindPort, int peerPort, boolean client) {
        this.bindPort = new InetSocketAddress("127.0.0.1", bindPort);
        this.peerPort = new InetSocketAddress("127.0.0.1", peerPort);
        this.isClient = client;
    }

    public void open() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(bindPort);
        channel.connect(peerPort);
        stream = new MAVLinkStream(schema, channel);
        if (isClient) {
            // Client initiates communication.
            MAVLinkMessage dummy = new MAVLinkMessage(schema, 0, 1, 51);
            System.out.println("Sending dummy message");
            stream.write(dummy);
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
        if (isOpened()) {
            try {
                stream.write(msg);
            } catch (IOException ignored) {
                // Silently ignore this exception, we likely just have nobody on this port yet/already
            }
        }
    }

    @Override
    public void update(long t) {
        while (isOpened()) {
            try {
                MAVLinkMessage msg = stream.read();
                if (msg == null) {
                    break;
                }
                sendMessage(msg);
            } catch (IOException e) {
                // Silently ignore this exception, we likely just have nobody on this port yet/already
                return;
            }
        }
    }
}
