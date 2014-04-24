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
import org.mavlink.io.LittleEndianDataInputStream;
import org.mavlink.io.LittleEndianDataOutputStream;
/**
 * Class msg_global_position_time
 * The filtered global position (e.g. fused GPS and other sensors, e.g. baro and accelerometer) and time. The position is in GPS-frame (right-handed, Z-up). Latitude and longitude encoded as scaled integers since the resolution of float is not sufficient.
 **/
public class msg_global_position_time extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_GLOBAL_POSITION_TIME = 127;
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
public void decode(LittleEndianDataInputStream dis) throws IOException {
  time = (long)dis.readLong();
  lat = (int)dis.readInt();
  lon = (int)dis.readInt();
  alt = (float)dis.readFloat();
  vx = (float)dis.readFloat();
  vy = (float)dis.readFloat();
  vz = (float)dis.readFloat();
  hdg = (int)dis.readUnsignedShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+34];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeLong(time);
  dos.writeInt((int)(lat&0x00FFFFFFFF));
  dos.writeInt((int)(lon&0x00FFFFFFFF));
  dos.writeFloat(alt);
  dos.writeFloat(vx);
  dos.writeFloat(vy);
  dos.writeFloat(vz);
  dos.writeShort(hdg&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 34);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[40] = crcl;
  buffer[41] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_GLOBAL_POSITION_TIME : " +   "  time="+time+  "  lat="+lat+  "  lon="+lon+  "  alt="+alt+  "  vx="+vx+  "  vy="+vy+  "  vz="+vz+  "  hdg="+hdg;}
}
