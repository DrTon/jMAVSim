package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkSchema;
import me.drton.jmavlib.mavlink.MAVLinkStream;
import me.drton.jmavlib.mavlink.MAVLinkMessage;

import java.io.IOException;
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
    private MAVLinkStream stream;
    private SocketAddress sendAddress;

    public UDPMavLinkPort(MAVLinkSchema schema) {
        super(schema);
        this.schema = schema;
    }

    public void open(SocketAddress address) throws IOException {
        channel = DatagramChannel.open();
        channel.socket().bind(address);
        channel.configureBlocking(false);
        rxBuffer.flip();
        stream = new MAVLinkStream(schema);
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
                channel.receive(rxBuffer);
                rxBuffer.flip();
                msg = stream.read(rxBuffer);
                if (msg == null) {
                    break;
                }
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
