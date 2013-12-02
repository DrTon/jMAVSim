package me.drton.jmavsim;

import org.mavlink.IMAVLinkMessage;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public class UDPMavLinkPort implements MAVLinkPort {
    private DatagramChannel channel;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer txBuffer = ByteBuffer.allocate(8192);
    private MAVLinkReader reader;
    private SocketAddress listenAddress;
    private SocketAddress sendAddress;

    public void open(SocketAddress address) throws IOException {
        this.listenAddress = address;
        channel = DatagramChannel.open();
        channel.socket().bind(address);
        channel.configureBlocking(false);
        rxBuffer.flip();
        DataInputStream inputStream = new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                if (rxBuffer.remaining() > 0) {
                    return rxBuffer.get() & 0xFF;
                }
                // Receive new packet
                rxBuffer.compact();
                channel.configureBlocking(true);
                SocketAddress addr = channel.receive(rxBuffer);
                if (addr != null)
                    sendAddress = addr;
                rxBuffer.flip();
                if (rxBuffer.remaining() > 0) {
                    return rxBuffer.get() & 0xFF;
                } else {
                    return -1;
                }
            }
        });
        reader = new MAVLinkReader(inputStream, IMAVLinkMessage.MAVPROT_PACKET_START_V10);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public boolean isOpened() {
        return channel.isOpen();
    }

    @Override
    public boolean hasNextMessage() throws IOException {
        if (!isOpened())
            return false;
        if (rxBuffer.remaining() == 0) {
            // Receive new packet
            rxBuffer.compact();
            channel.configureBlocking(false);
            SocketAddress addr = channel.receive(rxBuffer);
            if (addr != null)
                sendAddress = addr;
            rxBuffer.flip();
        }
        return rxBuffer.remaining() > 0;
    }

    @Override
    public MAVLinkMessage getNextMessage() throws IOException {
        if (isOpened())
            return reader.getNextMessage();
        else
            return null;
    }

    @Override
    public void sendMessage(MAVLinkMessage msg) throws IOException {
        if (isOpened()) {
            txBuffer.clear();
            txBuffer.put(msg.encode());
            txBuffer.flip();
            channel.send(txBuffer, sendAddress);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        UDPMavLinkPort port = new UDPMavLinkPort();
        port.open(new InetSocketAddress(14555));
        while (true) {
            if (port.hasNextMessage()) {
                MAVLinkMessage msg = port.getNextMessage();
                if (msg != null) {
                    System.out.println(msg);
                }
            } else {
                Thread.sleep(100);
            }
        }
    }
}
