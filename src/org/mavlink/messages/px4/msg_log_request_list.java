/**
 * Generated class : msg_log_request_list
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
 * Class msg_log_request_list
 * Request a list of available logs. On some systems calling this may stop on-board logging until LOG_REQUEST_END is called.
 **/
public class msg_log_request_list extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_LOG_REQUEST_LIST = 117;
  private static final long serialVersionUID = MAVLINK_MSG_ID_LOG_REQUEST_LIST;
  public msg_log_request_list(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_LOG_REQUEST_LIST;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 6;
}

  /**
   * First log id (0 for first available)
   */
  public int start;
  /**
   * Last log id (0xffff for last available)
   */
  public int end;
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
public void decode(ByteBuffer dis) throws IOException {
  start = (int)dis.getShort()&0x00FFFF;
  end = (int)dis.getShort()&0x00FFFF;
  target_system = (int)dis.get()&0x00FF;
  target_component = (int)dis.get()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+6];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.putShort((short)(start&0x00FFFF));
  dos.putShort((short)(end&0x00FFFF));
  dos.put((byte)(target_system&0x00FF));
  dos.put((byte)(target_component&0x00FF));
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 6);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[12] = crcl;
  buffer[13] = crch;
  return buffer;
}
}
