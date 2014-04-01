package me.drton.jmavsim;

import org.mavlink.IMAVLinkMessage;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * User: ton Date: 01.04.14 Time: 22:18
 */
public class TCPMAVLinkPort extends MAVLinkPort {
    private ServerSocketChannel serverChannel = null;
    private SocketChannel channel = null;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer txBuffer = ByteBuffer.allocate(8192);
    private MAVLinkReader reader;

    public void open(SocketAddress address) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(address);
        serverChannel.configureBlocking(false);
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
        if (channel == null) {
            // Wait for new connection
            channel = serverChannel.accept();
            if (channel != null) {
                System.out.println("Accepted connection from: " + channel.socket().getInetAddress());
                channel.finishConnect();
            }
        }
        if (channel != null && channel.isConnected()) {
            rxBuffer.compact();
            int n = channel.read(rxBuffer);
            if (n < 0) {
                System.out.println("Connection closed: " + channel.socket().getInetAddress());
                channel.close();
                channel = null;
            }
            rxBuffer.flip();
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
        if (serverChannel != null) {
            serverChannel.close();
        }
    }

    @Override
    public boolean isOpened() {
        return serverChannel != null && serverChannel.isOpen();
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        if (channel != null && channel.isConnected()) {
            txBuffer.clear();
            try {
                txBuffer.put(msg.encode());
                txBuffer.flip();
                channel.write(txBuffer);
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
}
