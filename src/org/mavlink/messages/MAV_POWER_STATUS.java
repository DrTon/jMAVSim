/**
 * Generated class : MAV_POWER_STATUS
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;
/**
 * Interface MAV_POWER_STATUS
 * Power supply status flags (bitmask)
 **/
public interface MAV_POWER_STATUS {
    /**
     * main brick power supply valid
     */
    public final static int MAV_POWER_STATUS_BRICK_VALID = 1;
    /**
     * main servo power supply valid for FMU
     */
    public final static int MAV_POWER_STATUS_SERVO_VALID = 2;
    /**
     * USB power is connected
     */
    public final static int MAV_POWER_STATUS_USB_CONNECTED = 4;
    /**
     * peripheral supply is in over-current state
     */
    public final static int MAV_POWER_STATUS_PERIPH_OVERCURRENT = 8;
    /**
     * hi-power peripheral supply is in over-current state
     */
    public final static int MAV_POWER_STATUS_PERIPH_HIPOWER_OVERCURRENT = 16;
    /**
     * Power status has changed since boot
     */
    public final static int MAV_POWER_STATUS_CHANGED = 32;
}
