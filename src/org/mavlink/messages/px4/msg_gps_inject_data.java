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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
public void decode(ByteBuffer dis) throws IOException {
  target_system = (int)dis.get()&0x00FF;
  target_component = (int)dis.get()&0x00FF;
  len = (int)dis.get()&0x00FF;
  for (int i=0; i<110; i++) {
    data[i] = (int)dis.get()&0x00FF;
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+113];
   ByteBuffer dos = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
  dos.put((byte)0xFE);
  dos.put((byte)(length & 0x00FF));
  dos.put((byte)(sequence & 0x00FF));
  dos.put((byte)(sysId & 0x00FF));
  dos.put((byte)(componentId & 0x00FF));
  dos.put((byte)(messageType & 0x00FF));
  dos.put((byte)(target_system&0x00FF));
  dos.put((byte)(target_component&0x00FF));
  dos.put((byte)(len&0x00FF));
  for (int i=0; i<110; i++) {
    dos.put((byte)(data[i]&0x00FF));
  }
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 113);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[119] = crcl;
  buffer[120] = crch;
  return buffer;
}
}
