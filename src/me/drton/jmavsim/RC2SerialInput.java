package me.drton.jmavsim;

import jssc.SerialPort;
import jssc.SerialPortException;
import me.drton.jmavlib.mavlink.MAVLinkMessage;
import me.drton.jmavlib.mavlink.MAVLinkSchema;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * RC2SerialInput driver, generates MAVLink MANUAL_CONTROL messages from RC2Serial converter.
 * Can be used to control SIL-simulated system with normal RC transmitter.
 * Arduino-based RC2Serial bridge required, see tools/RC2Serial/RC2Serial.ino.
 */
public class RC2SerialInput extends MAVLinkNode {
    static private final int PACKET_SIZE = 35;
    private int sysId;
    private int componentId;
    private SerialPort serialPort;
    private ByteChannel channel = null;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private double rcMin = 1000;
    private double rcTrim = 1500;
    private double rcMax = 2000;
    private boolean debug = false;

    public RC2SerialInput(MAVLinkSchema schema, int sysId, int componentId) {
        super(schema);
        this.sysId = sysId;
        this.componentId = componentId;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setRCCalibration(double rcMin, double rcTrim, double rcMax) {
        this.rcMin = rcMin;
        this.rcTrim = rcTrim;
        this.rcMax = rcMax;
    }

    public void open(String portName, int baudRate, int dataBits, int stopBits, int parity) throws IOException {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(baudRate, dataBits, stopBits, parity);
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
        channel = new SerialPortChannel(serialPort);
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

    @Override
    public void handleMessage(MAVLinkMessage msg) {
    }

    @Override
    public void update(long t) {
        if (isOpened()) {
            try {
                int r = channel.read(buffer);
                if (r <= 0) {
                    return;
                }
                buffer.flip();
                while (buffer.remaining() >= PACKET_SIZE) {
                    if (buffer.get() == (byte) 'R' && buffer.get() == (byte) 'C') {
                        double[] values = new double[16];
                        for (int i = 0; i < 16; i++) {
                            values[i] = (buffer.getShort() & 0xFFFF) / 10.0;
                        }
                        if (debug) {
                            System.out.printf("RC channels:");
                            for (int i = 0; i < 16; i++) {
                                System.out.printf("%s ", values[i]);
                            }
                            System.out.println();
                        }
                        int buttons = 0;
                        for (int i = 0; i < 12; i++) {
                            int sw = 0;
                            double v = values[i + 4];
                            if (v > 1700.0) {
                                sw = 1; // ON
                            } else if (v > 1300) {
                                sw = 2; // MIDDLE
                            } else if (v > 500) {
                                sw = 3; // OFF
                            }
                            buttons |= (sw << (i * 2));
                        }
                        MAVLinkMessage msg = new MAVLinkMessage(schema, "MANUAL_CONTROL", sysId, componentId);
                        msg.set("target", 0);
                        msg.set("x", scaleRC(values[1]));
                        msg.set("y", scaleRC(values[0]));
                        msg.set("z", scaleRC(values[3]));
                        msg.set("r", scaleRC(values[2]));
                        msg.set("buttons", buttons);
                        if (debug) {
                            System.out.println(msg);
                        }
                        sendMessage(msg);
                    }
                }
                buffer.compact();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private int scaleRC(double value) {
        if (value > rcTrim) {
            return Math.min(1000, (int) ((value - rcTrim) * 1000.0 / (rcMax - rcTrim)));
        } else {
            return Math.max(-1000, (int) ((value - rcTrim) * 1000.0 / (rcTrim - rcMin)));
        }
    }
}
