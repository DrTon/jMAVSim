/**
 * Generated class : msg_gps2_raw
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
 * Class msg_gps2_raw
 * Second GPS data. Coordinate frame is right-handed, Z-axis up (GPS frame).
 **/
public class msg_gps2_raw extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_GPS2_RAW = 124;
  private static final long serialVersionUID = MAVLINK_MSG_ID_GPS2_RAW;
  public msg_gps2_raw(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_GPS2_RAW;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 35;
}

  /**
   * Timestamp (microseconds since UNIX epoch or microseconds since system boot)
   */
  public long time_usec;
  /**
   * Latitude (WGS84), in degrees * 1E7
   */
  public long lat;
  /**
   * Longitude (WGS84), in degrees * 1E7
   */
  public long lon;
  /**
   * Altitude (WGS84), in meters * 1000 (positive for up)
   */
  public long alt;
  /**
   * Age of DGPS info
   */
  public long dgps_age;
  /**
   * GPS HDOP horizontal dilution of position in cm (m*100). If unknown, set to: UINT16_MAX
   */
  public int eph;
  /**
   * GPS VDOP vertical dilution of position in cm (m*100). If unknown, set to: UINT16_MAX
   */
  public int epv;
  /**
   * GPS ground speed (m/s * 100). If unknown, set to: UINT16_MAX
   */
  public int vel;
  /**
   * Course over ground (NOT heading, but direction of movement) in degrees * 100, 0.0..359.99 degrees. If unknown, set to: UINT16_MAX
   */
  public int cog;
  /**
   * 0-1: no fix, 2: 2D fix, 3: 3D fix. Some applications will not use the value of this field unless it is at least two, so always correctly fill in the fix.
   */
  public int fix_type;
  /**
   * Number of satellites visible. If unknown, set to 255
   */
  public int satellites_visible;
  /**
   * Number of DGPS satellites
   */
  public int dgps_numch;
/**
 * Decode message with raw data
 */
public void decode(ByteBuffer dis) throws IOException {
  time_usec = (long)dis.getLong();
  lat = (int)dis.getInt();
  lon = (int)dis.getInt();
  alt = (int)dis.getInt();
  dgps_age = (int)dis.getInt()&0x00FFFFFFFF;
  eph = (int)dis.getShort()&0x00FFFF;
  epv = (int)dis.getShort()&0x00FFFF;
  vel = (int)dis.getShort()&0x00FFFF;
  cog = (int)dis.getShort()&0x00FFFF;
  fix_type = (int)dis.get()&0x00FF;
  satellites_visible = (int)dis.get()&0x00FF;
  dgps_numch = (int)dis.get()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+35];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.putLong(time_usec);
  dos.putInt((int)(lat&0x00FFFFFFFF));
  dos.putInt((int)(lon&0x00FFFFFFFF));
  dos.putInt((int)(alt&0x00FFFFFFFF));
  dos.putInt((int)(dgps_age&0x00FFFFFFFF));
  dos.putShort((short)(eph&0x00FFFF));
  dos.putShort((short)(epv&0x00FFFF));
  dos.putShort((short)(vel&0x00FFFF));
  dos.putShort((short)(cog&0x00FFFF));
  dos.put((byte)(fix_type&0x00FF));
  dos.put((byte)(satellites_visible&0x00FF));
  dos.put((byte)(dgps_numch&0x00FF));
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 35);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[41] = crcl;
  buffer[42] = crch;
  return buffer;
}
}
