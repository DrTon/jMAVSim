package me.drton.jmavsim;

import org.mavlink.IMAVLinkMessage;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public class UDPMavLinkPort extends MAVLinkPort {
    private DatagramChannel channel = null;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer txBuffer = ByteBuffer.allocate(8192);
    private MAVLinkReader reader;
    private SocketAddress sendAddress;

    public void open(SocketAddress address) throws IOException {
        channel = DatagramChannel.open();
        channel.socket().bind(address);
        channel.configureBlocking(false);
        rxBuffer.flip();
        DataInputStream inputStream = new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                if (rxBuffer.remaining() == 0) {
                    // Receive new packet
                    fillBuffer();
                }
                if (rxBuffer.remaining() > 0) {
                    return rxBuffer.get() & 0xFF;
                } else {
                    return -1;
                }
            }

            @Override
            public int available() throws IOException {
                if (rxBuffer.remaining() == 0)
                    fillBuffer();
                return rxBuffer.remaining();
            }
        });
        reader = new MAVLinkReader(inputStream, IMAVLinkMessage.MAVPROT_PACKET_START_V10);
    }

    private void fillBuffer() throws IOException {
        // Receive new packet
        rxBuffer.compact();
        SocketAddress addr = channel.receive(rxBuffer);
        if (addr != null)
            sendAddress = addr;
        rxBuffer.flip();
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
            txBuffer.clear();
            try {
                txBuffer.put(msg.encode());
                txBuffer.flip();
                channel.send(txBuffer, sendAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(long t) {
        MAVLinkMessage msg;
        while (isOpened()) {
            msg = reader.getNextMessageWithoutBlocking();
            if (msg == null)
                break;
            sendMessage(msg);
        }
    }

    public void setSendAddress(SocketAddress sendAddress) {
        this.sendAddress = sendAddress;
    }
}
