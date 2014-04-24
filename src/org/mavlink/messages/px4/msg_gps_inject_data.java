/**
 * Generated class : msg_gps_inject_data
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
 * Class msg_gps_inject_data
 * data for injecting into the onboard GPS (used for DGPS)
 **/
public class msg_gps_inject_data extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_GPS_INJECT_DATA = 123;
  private static final long serialVersionUID = MAVLINK_MSG_ID_GPS_INJECT_DATA;
  public msg_gps_inject_data(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_GPS_INJECT_DATA;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 113;
}

  /**
   * System ID
   */
  public int target_system;
  /**
   * Component ID
   */
  public int target_component;
  /**
   * data length
   */
  public int len;
  /**
   * raw data (110 is enough for 12 satellites of RTCMv2)
   */
  public int[] data = new int[110];
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  target_system = (int)dis.readUnsignedByte()&0x00FF;
  target_component = (int)dis.readUnsignedByte()&0x00FF;
  len = (int)dis.readUnsignedByte()&0x00FF;
  for (int i=0; i<110; i++) {
    data[i] = (int)dis.readUnsignedByte()&0x00FF;
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+113];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeByte(target_system&0x00FF);
  dos.writeByte(target_component&0x00FF);
  dos.writeByte(len&0x00FF);
  for (int i=0; i<110; i++) {
    dos.writeByte(data[i]&0x00FF);
  }
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 113);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[119] = crcl;
  buffer[120] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_GPS_INJECT_DATA : " +   "  target_system="+target_system+  "  target_component="+target_component+  "  len="+len+  "  data="+data;}
}
