package me.drton.jmavsim;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.mavlink.IMAVLinkMessage;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.common.msg_heartbeat;
import org.mavlink.messages.common.msg_radio_status;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * User: ton Date: 28.11.13 Time: 23:30
 */
public class SerialMAVLinkPort implements MAVLinkPort {
    private SerialPort serialPort;
    private MAVLinkReader reader;

    public void open(String portName, int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(baudRate, dataBits, stopBits, parity);
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
        DataInputStream inputStream = new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                try {
                    byte[] b = serialPort.readBytes(1);
                    return b[0] & 0xff;
                } catch (SerialPortException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public int available() throws IOException {
                try {
                    return serialPort.getInputBufferBytesCount();
                } catch (SerialPortException e) {
                    throw new IOException(e);
                }
            }
        });
        reader = new MAVLinkReader(inputStream, IMAVLinkMessage.MAVPROT_PACKET_START_V10);
    }

    @Override
    public void close() throws IOException {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
        serialPort = null;
    }

    @Override
    public boolean isOpened() {
        return serialPort != null && serialPort.isOpened();
    }

    @Override
    public boolean hasNextMessage() throws IOException {
        try {
            return isOpened() && serialPort.getInputBufferBytesCount() > 0;
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
    }

    @Override
    public MAVLinkMessage getNextMessage(boolean blocking) throws IOException {
        if (isOpened())
            return blocking ? reader.getNextMessage() : reader.getNextMessageWithoutBlocking();
        else
            return null;
    }

    @Override
    public void sendMessage(MAVLinkMessage msg) throws IOException {
        if (isOpened()) {
            try {
                serialPort.writeBytes(msg.encode());
            } catch (SerialPortException e) {
                throw new IOException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        SerialMAVLinkPort port = new SerialMAVLinkPort();
        port.open("/dev/tty.usbserial-DN006L8F", 57600, 8, 1, 0);
        long last_hrt = 0;
        while (true) {
            MAVLinkMessage msg = port.getNextMessage(true);
            if (msg != null) {
                if (msg instanceof msg_radio_status)
                    System.out.println(msg);
            }
            if (System.currentTimeMillis() - last_hrt > 1000) {
                msg_heartbeat heartbeat = new msg_heartbeat(255, 0);
                System.out.println("Send " + heartbeat);
                port.sendMessage(heartbeat);
                last_hrt = System.currentTimeMillis();
            }
        }
    }
}
