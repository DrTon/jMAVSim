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
public class MAVLinkPort {
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
        });
        reader = new MAVLinkReader(inputStream, IMAVLinkMessage.MAVPROT_PACKET_START_V10);
    }

    public void close() throws IOException {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
        serialPort = null;
    }

    public boolean isOpened() {
        return serialPort != null && serialPort.isOpened();
    }

    public boolean hasNextMessage() throws IOException {
        try {
            return isOpened() && serialPort.getInputBufferBytesCount() > 0;
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
    }

    public MAVLinkMessage getNextMessage() throws IOException {
        if (isOpened())
            return reader.getNextMessage();
        else
            return null;
    }

    public void sendMessage(MAVLinkMessage msg) throws IOException {
        if (isOpened()) {
            try {
                serialPort.writeBytes(msg.encode());
            } catch (SerialPortException e) {
                throw new IOException(e);
            }
        }
    }
}
