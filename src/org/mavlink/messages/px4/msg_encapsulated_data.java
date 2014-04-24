/**
 * Generated class : msg_encapsulated_data
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
 * Class msg_encapsulated_data
 * 
 **/
public class msg_encapsulated_data extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_ENCAPSULATED_DATA = 131;
  private static final long serialVersionUID = MAVLINK_MSG_ID_ENCAPSULATED_DATA;
  public msg_encapsulated_data(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_ENCAPSULATED_DATA;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 255;
}

  /**
   * sequence number (starting with 0 on every transmission)
   */
  public int seqnr;
  /**
   * image data bytes
   */
  public int[] data = new int[253];
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  seqnr = (int)dis.readUnsignedShort()&0x00FFFF;
  for (int i=0; i<253; i++) {
    data[i] = (int)dis.readUnsignedByte()&0x00FF;
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+255];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeShort(seqnr&0x00FFFF);
  for (int i=0; i<253; i++) {
    dos.writeByte(data[i]&0x00FF);
  }
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 255);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[261] = crcl;
  buffer[262] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_ENCAPSULATED_DATA : " +   "  seqnr="+seqnr+  "  data="+data;}
}
