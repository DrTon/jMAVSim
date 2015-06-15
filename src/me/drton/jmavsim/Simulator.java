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
import java.net.InetSocketAddress;

/**
 * User: ton Date: 26.11.13 Time: 12:33
 */
public class Simulator {
    private World world;
    private int sleepInterval = 4;  // Main loop interval, in ms
    private int simDelayMax = 500;  // Max delay between simulated and real time to skip samples in simulator, in ms

    public Simulator() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Create world
        world = new World();
        // Set global reference point
        world.setGlobalReference(new LatLonAlt(55.753395, 37.625427, 0.0));

        MAVLinkSchema schema = new MAVLinkSchema("mavlink/message_definitions/common.xml");

        // Create MAVLink connections
        MAVLinkConnection connSIL = new MAVLinkConnection(world);
        world.addObject(connSIL);

        // Create ports
        // UDP port: connection to autopilot
        UDPMavLinkPort silPort = new UDPMavLinkPort(schema);
        connSIL.addNode(silPort);
        // Serial RC input port
        RC2SerialInput rcInputPort = new RC2SerialInput(schema, 1, 51);
        rcInputPort.setRCCalibration(1100.0, 1518.0, 1936.0);
        connSIL.addNode(rcInputPort);

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
        connSIL.addNode(hilSystem);
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
        silPort.open(new InetSocketAddress("127.0.0.1", 14560), new InetSocketAddress("127.0.0.1", 14565));
        rcInputPort.open("/dev/tty.usbmodem1421", 115200, 8, 1, 0);

        // Run
        try {
            run();
        } catch (InterruptedException e) {
            System.out.println("Exit");
        }

        // Close ports
        silPort.close();
        rcInputPort.close();
    }

    public void run() throws IOException, InterruptedException {
        long t = System.currentTimeMillis();
        while (true) {
            world.update(t);
            long now = System.currentTimeMillis();
            long nextRun = t + sleepInterval;
            long timeLeft = nextRun - now;
            if (timeLeft < -simDelayMax) {
                System.out.printf("Skipped %s ms\n", -timeLeft);
                nextRun = now;
            } else if (timeLeft > 0) {
                Thread.sleep(timeLeft);
            }
            t = nextRun;
        }
    }

    public static void main(String[] args)
            throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        new Simulator();
    }
}
