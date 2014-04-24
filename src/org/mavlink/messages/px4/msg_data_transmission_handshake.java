/**
 * Generated class : msg_data_transmission_handshake
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
 * Class msg_data_transmission_handshake
 * 
 **/
public class msg_data_transmission_handshake extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE = 130;
  private static final long serialVersionUID = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;
  public msg_data_transmission_handshake(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 13;
}

  /**
   * total data size in bytes (set on ACK only)
   */
  public long size;
  /**
   * Width of a matrix or image
   */
  public int width;
  /**
   * Height of a matrix or image
   */
  public int height;
  /**
   * number of packets beeing sent (set on ACK only)
   */
  public int packets;
  /**
   * type of requested/acknowledged data (as defined in ENUM DATA_TYPES in mavlink/include/mavlink_types.h)
   */
  public int type;
  /**
   * payload size per packet (normally 253 byte, see DATA field size in message ENCAPSULATED_DATA) (set on ACK only)
   */
  public int payload;
  /**
   * JPEG quality out of [1,100]
   */
  public int jpg_quality;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  size = (int)dis.readInt()&0x00FFFFFFFF;
  width = (int)dis.readUnsignedShort()&0x00FFFF;
  height = (int)dis.readUnsignedShort()&0x00FFFF;
  packets = (int)dis.readUnsignedShort()&0x00FFFF;
  type = (int)dis.readUnsignedByte()&0x00FF;
  payload = (int)dis.readUnsignedByte()&0x00FF;
  jpg_quality = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+13];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(size&0x00FFFFFFFF));
  dos.writeShort(width&0x00FFFF);
  dos.writeShort(height&0x00FFFF);
  dos.writeShort(packets&0x00FFFF);
  dos.writeByte(type&0x00FF);
  dos.writeByte(payload&0x00FF);
  dos.writeByte(jpg_quality&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 13);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[19] = crcl;
  buffer[20] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE : " +   "  size="+size+  "  width="+width+  "  height="+height+  "  packets="+packets+  "  type="+type+  "  payload="+payload+  "  jpg_quality="+jpg_quality;}
}
