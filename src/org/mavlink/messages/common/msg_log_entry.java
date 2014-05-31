/**
 * Generated class : msg_log_entry
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
 * Class msg_log_entry
 * Reply to LOG_REQUEST_LIST
 **/
public class msg_log_entry extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_LOG_ENTRY = 118;
  private static final long serialVersionUID = MAVLINK_MSG_ID_LOG_ENTRY;
  public msg_log_entry(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_LOG_ENTRY;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 14;
}

  /**
   * UTC timestamp of log in seconds since 1970, or 0 if not available
   */
  public long time_utc;
  /**
   * Size of the log (may be approximate) in bytes
   */
  public long size;
  /**
   * Log id
   */
  public int id;
  /**
   * Total number of logs
   */
  public int num_logs;
  /**
   * High log number
   */
  public int last_log_num;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  time_utc = (int)dis.readInt()&0x00FFFFFFFF;
  size = (int)dis.readInt()&0x00FFFFFFFF;
  id = (int)dis.readUnsignedShort()&0x00FFFF;
  num_logs = (int)dis.readUnsignedShort()&0x00FFFF;
  last_log_num = (int)dis.readUnsignedShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+14];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(time_utc&0x00FFFFFFFF));
  dos.writeInt((int)(size&0x00FFFFFFFF));
  dos.writeShort(id&0x00FFFF);
  dos.writeShort(num_logs&0x00FFFF);
  dos.writeShort(last_log_num&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 14);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[20] = crcl;
  buffer[21] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_LOG_ENTRY : " +   "  time_utc="+time_utc+  "  size="+size+  "  id="+id+  "  num_logs="+num_logs+  "  last_log_num="+last_log_num;}
}
