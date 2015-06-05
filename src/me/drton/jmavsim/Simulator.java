package me.drton.jmavsim;

import me.drton.jmavlib.geo.LatLonAlt;
import me.drton.jmavlib.mavlink.MAVLinkSchema;
import me.drton.jmavsim.vehicle.AbstractMulticopter;
import me.drton.jmavsim.vehicle.Quadcopter;
import org.xml.sax.SAXException;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * User: ton Date: 26.11.13 Time: 12:33
 */
public class Simulator {

    public static boolean USE_SERIAL_PORT = false;
    public static final int DEFAULT_AUTOPILOT_PORT = 14550;
    public static final int DEFAULT_QGC_PORT = 14555;
    public static final String DEFAULT_SERIAL_PATH = "/dev/tty.usbmodem1";
    public static final int DEFAULT_SERIAL_BAUD_RATE = 230400;

    private static int autopilotPort = DEFAULT_AUTOPILOT_PORT;
    private static int qgcPort = DEFAULT_QGC_PORT;
    private static String serialPath = DEFAULT_SERIAL_PATH;
    private static int serialBaudRate = DEFAULT_SERIAL_BAUD_RATE;

    private World world;
    private int sleepInterval = 5;  // Main loop interval, in ms

    public Simulator() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Create world
        world = new World();
        // Set global reference point
        world.setGlobalReference(new LatLonAlt(55.753395, 37.625427, 0.0));

        MAVLinkSchema schema = new MAVLinkSchema("mavlink/message_definitions/common.xml");

        // Create MAVLink connections
        MAVLinkConnection connHIL = new MAVLinkConnection(world);
        world.addObject(connHIL);
        MAVLinkConnection connCommon = new MAVLinkConnection(world);
        // Don't spam ground station with HIL messages
        connCommon.addSkipMessage(schema.getMessageDefinition("HIL_CONTROLS").id);
        connCommon.addSkipMessage(schema.getMessageDefinition("HIL_SENSOR").id);
        connCommon.addSkipMessage(schema.getMessageDefinition("HIL_GPS").id);
        world.addObject(connCommon);

        // Create ports
        MAVLinkPort autopilotMavLinkPort;
        if (USE_SERIAL_PORT) {
            //Serial port: connection to autopilot over serial.
            SerialMAVLinkPort port = new SerialMAVLinkPort(schema);
            port.setup(serialPath, serialBaudRate, 8, 1, 0);
            autopilotMavLinkPort = port;
        } else {
            UDPMavLinkPort port = new UDPMavLinkPort(schema);
            port.setup(0, autopilotPort, true); // default source port 0 for autopilot, which is a client of JMAVSim
            autopilotMavLinkPort = port;
        }

        // allow HIL and GCS to talk to this port
        connHIL.addNode(autopilotMavLinkPort);
        connCommon.addNode(autopilotMavLinkPort);
        // UDP port: connection to ground station
        UDPMavLinkPort udpGCMavLinkPort = new UDPMavLinkPort(schema);
        udpGCMavLinkPort.setup(qgcPort, autopilotPort, false);
        connCommon.addNode(udpGCMavLinkPort);

        // Create environment
        SimpleEnvironment simpleEnvironment = new SimpleEnvironment(world);
        Vector3d magField = new Vector3d(0.2f, 0.0f, 0.5f);
        Matrix3d magDecl = new Matrix3d();
        magDecl.rotZ(11.0 / 180.0 * Math.PI);
        magDecl.transform(magField);
        simpleEnvironment.setMagField(magField);
        //simpleEnvironment.setWind(new Vector3d(0.0, 5.0, 0.0));
        simpleEnvironment.setGroundLevel(0.0f);
        world.addObject(simpleEnvironment);

        // Create vehicle with sensors
        Vector3d gc = new Vector3d(0.0, 0.0, 0.0);  // gravity center
        AbstractMulticopter vehicle = new Quadcopter(world, "models/3dr_arducopter_quad_x.obj", "x", 0.33 / 2, 4.0,
                0.05, 0.005, gc);
        vehicle.setMass(0.8);
        Matrix3d I = new Matrix3d();
        // Moments of inertia
        I.m00 = 0.005;  // X
        I.m11 = 0.005;  // Y
        I.m22 = 0.009;  // Z
        vehicle.setMomentOfInertia(I);
        SimpleSensors sensors = new SimpleSensors();
        sensors.setGPSDelay(200);
        sensors.setGPSStartTime(System.currentTimeMillis() + 20000);
        vehicle.setSensors(sensors);
        vehicle.setDragMove(0.02);
        //v.setDragRotate(0.1);

        // Create MAVLink HIL system
        // SysId should be the same as autopilot, ComponentId should be different!
        MAVLinkHILSystem hilSystem = new MAVLinkHILSystem(schema, 1, 51, vehicle);
        connHIL.addNode(hilSystem);
        world.addObject(vehicle);

        // Create 3D visualizer
        Visualizer3D visualizer = new Visualizer3D(world);

        // Put camera on vehicle (FPV)
        visualizer.setViewerPositionObject(vehicle);
        visualizer.setViewerPositionOffset(new Vector3d(-0.6f, 0.0f, -0.3f));   // Offset from vehicle center

        // Put camera on vehicle with gimbal
        /*
        CameraGimbal2D gimbal = new CameraGimbal2D(world);
        gimbal.setBaseObject(vehicle);
        gimbal.setPitchChannel(4);  // Control gimbal from autopilot
        gimbal.setPitchScale(1.57); // +/- 90deg
        world.addObject(gimbal);
        visualizer.setViewerPositionObject(gimbal);
        */

        // Put camera on static point and point to vehicle
        /*
        visualizer.setViewerPosition(new Vector3d(-5.0, 0.0, -1.7));
        visualizer.setViewerTargetObject(vehicle);
        */

        // Open ports
        //serialMAVLinkPort.setDebug(true);
        autopilotMavLinkPort.open();
        //serialMAVLinkPort.sendRaw("\nsh /etc/init.d/rc.usb\n".getBytes());
        //udpMavLinkPort.setDebug(true);
        udpGCMavLinkPort.open();
        //udpMavLinkPort.open(new InetSocketAddress(14555), new InetSocketAddress(14550));

        // Run
        try {
            run();
        } catch (InterruptedException e) {
            System.out.println("Exit");
        }

        // Close ports
        autopilotMavLinkPort.close();
        udpGCMavLinkPort.close();
    }

    public void run() throws IOException, InterruptedException {
        long nextRun = System.currentTimeMillis() + sleepInterval;
        while (true) {
            long t = System.currentTimeMillis();
            world.update(t);
            long timeLeft = Math.max(sleepInterval / 4, nextRun - System.currentTimeMillis());
            nextRun = Math.max(t + sleepInterval / 4, nextRun + sleepInterval);
            Thread.sleep(timeLeft);
        }
    }

    public static void main(String[] args)
            throws InterruptedException, IOException, ParserConfigurationException, SAXException {

        String usageString = "java -cp lib/*:out/production/jmavsim.jar me.drton.jmavsim.Simulator " +
                "[-udp <autopilot port> <qgc port> | -serial <path> <baudRate>]";
        // default is to use UDP.
        if (args.length == 0) {
            USE_SERIAL_PORT = false;
        }
        if (args.length > 0 && args.length < 3 || args.length > 3) {
            System.err.println("Incorrect number of arguments. \n Usage: " + usageString);
            return;
        }

        int i = 0;
        while (i < args.length) {
            String arg = args[i++];
            if (arg.equalsIgnoreCase("-udp")) {
                USE_SERIAL_PORT = false;
                try {
                    autopilotPort = Integer.parseInt(args[i++]);
                    qgcPort = Integer.parseInt(args[i++]);
                } catch (NumberFormatException e) {
                    System.out.println("Expected: " + usageString + ", got: " + e.toString());
                    return;
                }
            } else if (arg.equals("-serial")) {
                USE_SERIAL_PORT = true;
                try {
                    serialPath = args[i++];
                    serialBaudRate = Integer.parseInt(args[i++]);
                } catch (NumberFormatException e) {
                    System.out.println("Expected: " + usageString + ", got: " + e.toString());
                    return;
                }
            }
         }

        if (i != args.length) {
            System.err.println("Usage: " + usageString);
            return;
        } else { System.out.println("Success!"); }

        new Simulator();
    }
}
