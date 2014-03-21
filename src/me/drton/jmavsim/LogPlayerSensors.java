package me.drton.jmavsim;

import me.drton.flightplot.FormatErrorException;
import me.drton.flightplot.LogReader;
import me.drton.flightplot.PX4LogReader;

import javax.vecmath.Vector3d;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ton Date: 09.03.14 Time: 18:37
 */
public class LogPlayerSensors implements Sensors {
    private LogReader logReader = null;
    private long logStart = 0;
    private long logStartDelay = 10000;
    private long logT = 0;
    private Vector3d acc = new Vector3d();
    private Vector3d gyro = new Vector3d();
    private Vector3d mag = new Vector3d();
    private double baroAlt;
    private GlobalPosition gps = new GlobalPosition();

    void openLog(String fileName) throws IOException, FormatErrorException {
        logReader = new PX4LogReader(fileName);
        logStart = System.currentTimeMillis() - logReader.getStartMicroseconds() / 1000 + logStartDelay;
    }

    @Override
    public void setObject(MechanicalObject object) {
    }

    @Override
    public Vector3d getAcc() {
        return acc;
    }

    @Override
    public Vector3d getGyro() {
        return gyro;
    }

    @Override
    public Vector3d getMag() {
        return mag;
    }

    @Override
    public double getPressureAlt() {
        return baroAlt;
    }

    @Override
    public GlobalPosition getGlobalPosition() {
        return gps;
    }

    @Override
    public void update(long t) {
        if (logReader != null) {
            Map<String, Object> logData = new HashMap<String, Object>();
            while (logStart + logT < t) {
                try {
                    logT = logReader.readUpdate(logData) / 1000;
                } catch (EOFException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (FormatErrorException e) {
                    e.printStackTrace();
                    break;
                }
            }
            if (logData.containsKey("IMU.AccX") &&
                    logData.containsKey("IMU.AccY") &&
                    logData.containsKey("IMU.AccZ")) {
                acc.set((Float) logData.get("IMU.AccX"), (Float) logData.get("IMU.AccY"),
                        (Float) logData.get("IMU.AccZ"));
            }
            if (logData.containsKey("IMU.GyroX") &&
                    logData.containsKey("IMU.GyroY") &&
                    logData.containsKey("IMU.GyroZ")) {
                gyro.set((Float) logData.get("IMU.GyroX"), (Float) logData.get("IMU.GyroY"),
                        (Float) logData.get("IMU.GyroZ"));
            }
            if (logData.containsKey("IMU.MagX") &&
                    logData.containsKey("IMU.MagY") &&
                    logData.containsKey("IMU.MagZ")) {
                mag.set((Float) logData.get("IMU.MagX"), (Float) logData.get("IMU.MagY"),
                        (Float) logData.get("IMU.MagZ"));
            }
            if (logData.containsKey("SENS.BaroAlt")) {
                baroAlt = (Float) logData.get("SENS.BaroAlt");
            }
            if (logData.containsKey("GPS.Lat") &&
                    logData.containsKey("GPS.Lon") &&
                    logData.containsKey("GPS.Alt")) {
                gps.lat = (Double) logData.get("GPS.Lat");
                gps.lon = (Double) logData.get("GPS.Lon");
                gps.alt = (Float) logData.get("GPS.Alt");
                gps.eph = (Float) logData.get("GPS.EPH");
                gps.epv = (Float) logData.get("GPS.EPV");
                gps.vn = (Float) logData.get("GPS.VelN");
                gps.ve = (Float) logData.get("GPS.VelE");
                gps.vd = (Float) logData.get("GPS.VelD");
                gps.fix = (Integer) logData.get("GPS.FixType");
                gps.time = (Long) logData.get("GPS.GPSTime");
            }
        }
    }
}
