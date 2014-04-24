/**
 * Generated class : MAV_FRAME
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;
/**
 * Interface MAV_FRAME
 * 
 **/
public interface MAV_FRAME {
    /**
     * Global coordinate frame, WGS84 coordinate system. First value / x: latitude, second value / y: longitude, third value / z: positive altitude over mean sea level (MSL)
     */
    public final static int MAV_FRAME_GLOBAL = 0;
    /**
     * Local coordinate frame, Z-up (x: north, y: east, z: down).
     */
    public final static int MAV_FRAME_LOCAL_NED = 1;
    /**
     * NOT a coordinate frame, indicates a mission command.
     */
    public final static int MAV_FRAME_MISSION = 2;
    /**
     * Global coordinate frame, WGS84 coordinate system, relative altitude over ground with respect to the home position. First value / x: latitude, second value / y: longitude, third value / z: positive altitude with 0 being at the altitude of the home location.
     */
    public final static int MAV_FRAME_GLOBAL_RELATIVE_ALT = 3;
    /**
     * Local coordinate frame, Z-down (x: east, y: north, z: up)
     */
    public final static int MAV_FRAME_LOCAL_ENU = 4;
    /**
     * Global coordinate frame with some fields as scaled integers, WGS84 coordinate system. First value / x: latitude, second value / y: longitude, third value / z: positive altitude over mean sea level (MSL). Lat / Lon are scaled * 1E7 to avoid floating point accuracy limitations.
     */
    public final static int MAV_FRAME_GLOBAL_INT = 5;
    /**
     * Global coordinate frame with some fields as scaled integers, WGS84 coordinate system, relative altitude over ground with respect to the home position. First value / x: latitude, second value / y: longitude, third value / z: positive altitude with 0 being at the altitude of the home location. Lat / Lon are scaled * 1E7 to avoid floating point accuracy limitations.
     */
    public final static int MAV_FRAME_GLOBAL_RELATIVE_ALT_INT = 6;
}
