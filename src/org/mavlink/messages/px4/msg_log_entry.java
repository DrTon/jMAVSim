/**
 * Generated class : msg_log_entry
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
public void decode(ByteBuffer dis) throws IOException {
  time_utc = (int)dis.getInt()&0x00FFFFFFFF;
  size = (int)dis.getInt()&0x00FFFFFFFF;
  id = (int)dis.getShort()&0x00FFFF;
  num_logs = (int)dis.getShort()&0x00FFFF;
  last_log_num = (int)dis.getShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+14];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.putInt((int)(time_utc&0x00FFFFFFFF));
  dos.putInt((int)(size&0x00FFFFFFFF));
  dos.putShort((short)(id&0x00FFFF));
  dos.putShort((short)(num_logs&0x00FFFF));
  dos.putShort((short)(last_log_num&0x00FFFF));
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 14);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[20] = crcl;
  buffer[21] = crch;
  return buffer;
}
}
