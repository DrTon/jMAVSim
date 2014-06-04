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
    private Visualizer visualizer;
    private int sleepInterval = 10;
    private int visualizerSleepInterval = 20;
    private long nextRun = 0;

    public Simulator() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Create world
        world = new World();
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
        // Serial port: connection to autopilot
        SerialMAVLinkPort serialMAVLinkPort = new SerialMAVLinkPort(schema);
        connCommon.addNode(serialMAVLinkPort);
        connHIL.addNode(serialMAVLinkPort);
        // UDP port: connection to ground station
        UDPMavLinkPort udpMavLinkPort = new UDPMavLinkPort(schema);
        connCommon.addNode(udpMavLinkPort);

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

        // Create target
        SimpleTarget target = new SimpleTarget(world, 0.3);
        long t = System.currentTimeMillis();
        target.setTrajectory(new Vector3d(5.0, 0.0, -2.0), new Vector3d(5.0, 100.0, -2.0), t + 20000, t + 50000);
        connCommon.addNode(new MAVLinkTargetSystem(2, 1, target));
        world.addObject(target);

        // Create MAVLink control
        /*
        // TargetComponentID should be 190
        MAVLinkControl mavLinkControl = new MAVLinkControl(5, 1, 1, 190);
        mavLinkControl.loadMission("/path/to/some/mission.txt");
        mavLinkControl.setMissionSendTime(System.currentTimeMillis() + 15000);
        connHIL.addNode(mavLinkControl);
        */

        // Create visualizer
        visualizer = new Visualizer(world);
        // Put camera on vehicle (FPV)
        /*
        visualizer.setViewerPositionObject(vehicle);   // Without gimbal
         */
        // Put camera on vehicle with gimbal
        // Create camera gimbal
        CameraGimbal2D gimbal = new CameraGimbal2D(world);
        gimbal.setBaseObject(vehicle);
        gimbal.setPitchChannel(4);
        gimbal.setPitchScale(1.57); // +/- 90deg
        world.addObject(gimbal);
        visualizer.setViewerPositionObject(gimbal);      // With gimbal
        // Put camera on static point and point to vehicle
        /*
        visualizer.setViewerPosition(new Vector3d(-5.0, 0.0, -1.7));
        visualizer.setViewerTargetObject(vehicle);
        visualizer.setAutoRotate(true);
        */

        // Open ports
        serialMAVLinkPort.open("/dev/tty.usbmodem1", 230400, 8, 1, 0);
        serialMAVLinkPort.sendRaw("\nsh /etc/init.d/rc.usb\n".getBytes());
        udpMavLinkPort.open(new InetSocketAddress(14555));

        // Run
        try {
            run();
        } catch (InterruptedException e) {
            System.out.println("Exit");
        }

        // Close ports
        serialMAVLinkPort.close();
        udpMavLinkPort.close();
    }

    public void run() throws IOException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    visualizer.update();
                    try {
                        Thread.sleep(visualizerSleepInterval);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }).start();
        nextRun = System.currentTimeMillis() + sleepInterval;
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
        new Simulator();
    }
}
