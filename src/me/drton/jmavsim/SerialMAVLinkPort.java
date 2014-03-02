package me.drton.jmavsim;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.mavlink.IMAVLinkMessage;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: ton Date: 28.11.13 Time: 23:30
 */
public class SerialMAVLinkPort extends MAVLinkPort {
    private SerialPort serialPort;
    private MAVLinkReader reader;
    private long bytesReceived = 0;

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
                    bytesReceived++;
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
    public void handleMessage(MAVLinkMessage msg) {
        if (isOpened()) {
            try {
                serialPort.writeBytes(msg.encode());
            } catch (Exception e) {
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

    public static void main(String[] args) throws InterruptedException, IOException {
        SerialMAVLinkPort port = new SerialMAVLinkPort();
        //port.open("/dev/tty.usbserial-DC008SB7", 57600, 8, 1, 0);
        port.open("/dev/tty.usbmodem1", 57600, 8, 1, 0);
        long bytesReceivedPrev = 0;
        long timePrev = 0;
        while (true) {
            long t = System.currentTimeMillis();
            port.update(t);
            if (t - timePrev > 2000) {
                long bytesReceived = port.bytesReceived;
                double b = (bytesReceived - bytesReceivedPrev) * 1000.0 / (t - timePrev);
                bytesReceivedPrev = bytesReceived;
                timePrev = t;
                System.out.printf("%.3f bytes/s\n", b);
            }
        }
    }

}
