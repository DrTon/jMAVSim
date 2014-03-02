/**
 * Generated class : msg_global_position_time
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.px4;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.IMAVLinkCRC;
import org.mavlink.MAVLinkCRC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Class msg_global_position_time
 * The filtered global position (e.g. fused GPS and other sensors, e.g. baro and accelerometer) and time. The position is in GPS-frame (right-handed, Z-up). Latitude and longitude encoded as scaled integers since the resolution of float is not sufficient.
 **/
public class msg_global_position_time extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_GLOBAL_POSITION_TIME = 126;
  private static final long serialVersionUID = MAVLINK_MSG_ID_GLOBAL_POSITION_TIME;
  public msg_global_position_time(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_GLOBAL_POSITION_TIME;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 34;
}

  /**
   * GPS time (microseconds since UNIX epoch)
   */
  public long time;
  /**
   * Latitude, expressed as * 1E7
   */
  public long lat;
  /**
   * Longitude, expressed as * 1E7
   */
  public long lon;
  /**
   * Altitude in meters, expressed as * 1000 (millimeters), above MSL
   */
  public float alt;
  /**
   * Ground X Speed (Latitude), expressed as m/s * 100
   */
  public float vx;
  /**
   * Ground Y Speed (Longitude), expressed as m/s * 100
   */
  public float vy;
  /**
   * Ground Z Speed (Altitude), expressed as m/s * 100
   */
  public float vz;
  /**
   * Compass heading in degrees * 100, 0.0..359.99 degrees. If unknown, set to: UINT16_MAX
   */
  public int hdg;
/**
 * Decode message with raw data
 */
public void decode(ByteBuffer dis) throws IOException {
  time = (long)dis.getLong();
  lat = (int)dis.getInt();
  lon = (int)dis.getInt();
  alt = (float)dis.getFloat();
  vx = (float)dis.getFloat();
  vy = (float)dis.getFloat();
  vz = (float)dis.getFloat();
  hdg = (int)dis.getShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+34];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.putLong(time);
  dos.putInt((int)(lat&0x00FFFFFFFF));
  dos.putInt((int)(lon&0x00FFFFFFFF));
  dos.putFloat(alt);
  dos.putFloat(vx);
  dos.putFloat(vy);
  dos.putFloat(vz);
  dos.putShort((short)(hdg&0x00FFFF));
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 34);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[40] = crcl;
  buffer[41] = crch;
  return buffer;
}
}
