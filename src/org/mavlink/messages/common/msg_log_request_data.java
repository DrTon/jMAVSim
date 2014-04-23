/**
 * Generated class : msg_log_request_data
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.common;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.IMAVLinkCRC;
import org.mavlink.MAVLinkCRC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.mavlink.io.LittleEndianDataInputStream;
import org.mavlink.io.LittleEndianDataOutputStream;
/**
 * Class msg_log_request_data
 * Request a chunk of a log
 **/
public class msg_log_request_data extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_LOG_REQUEST_DATA = 119;
  private static final long serialVersionUID = MAVLINK_MSG_ID_LOG_REQUEST_DATA;
  public msg_log_request_data(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_LOG_REQUEST_DATA;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 12;
}

  /**
   * Offset into the log
   */
  public long ofs;
  /**
   * Number of bytes
   */
  public long count;
  /**
   * Log id (from LOG_ENTRY reply)
   */
  public int id;
  /**
   * System ID
   */
  public int target_system;
  /**
   * Component ID
   */
  public int target_component;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  ofs = (int)dis.readInt()&0x00FFFFFFFF;
  count = (int)dis.readInt()&0x00FFFFFFFF;
  id = (int)dis.readUnsignedShort()&0x00FFFF;
  target_system = (int)dis.readUnsignedByte()&0x00FF;
  target_component = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+12];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(ofs&0x00FFFFFFFF));
  dos.writeInt((int)(count&0x00FFFFFFFF));
  dos.writeShort(id&0x00FFFF);
  dos.writeByte(target_system&0x00FF);
  dos.writeByte(target_component&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 12);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[18] = crcl;
  buffer[19] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_LOG_REQUEST_DATA : " +   "  ofs="+ofs+  "  count="+count+  "  id="+id+  "  target_system="+target_system+  "  target_component="+target_component;}
}
