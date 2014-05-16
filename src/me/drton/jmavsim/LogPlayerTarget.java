package me.drton.jmavsim;

import me.drton.jmavlib.log.FormatErrorException;
import me.drton.jmavlib.log.LogReader;
import me.drton.jmavlib.log.PX4LogReader;

import javax.vecmath.Vector3d;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ton Date: 04.05.14 Time: 23:41
 */
public class LogPlayerTarget extends Target {
    private LogReader logReader = null;
    private long logStart = 0;
    private long timeStart = 0;
    private long logT = 0;
    private Vector3d positionOffset = new Vector3d();

    public LogPlayerTarget(World world, double size) throws FileNotFoundException {
        super(world, size);
    }

    void openLog(String fileName) throws IOException, FormatErrorException {
        logReader = new PX4LogReader(fileName);
        logStart = logReader.getStartMicroseconds() / 1000;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setPositionOffset(Vector3d positionOffset) {
        this.positionOffset = positionOffset;
    }

    @Override
    public void update(long t) {
        if (logReader != null) {
            Map<String, Object> logData = new HashMap<String, Object>();
            while (timeStart - logStart + logT < t) {
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
            if (logData.containsKey("LPOS.X") &&
                    logData.containsKey("LPOS.Y") &&
                    logData.containsKey("LPOS.Z")) {
                position.add(new Vector3d((Float) logData.get("LPOS.X"), (Float) logData.get("LPOS.Y"),
                        (Float) logData.get("LPOS.Z")), positionOffset);
            }
            if (logData.containsKey("LPOS.VX") &&
                    logData.containsKey("LPOS.VY") &&
                    logData.containsKey("LPOS.VZ")) {
                velocity.set((Float) logData.get("LPOS.VX"), (Float) logData.get("LPOS.VY"),
                        (Float) logData.get("LPOS.VZ"));
            }
        }
        super.update(t);
    }
}
