/**
 * Generated class : msg_power_status
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
 * Class msg_power_status
 * Power supply status
 **/
public class msg_power_status extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_POWER_STATUS = 125;
  private static final long serialVersionUID = MAVLINK_MSG_ID_POWER_STATUS;
  public msg_power_status(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_POWER_STATUS;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 6;
}

  /**
   * 5V rail voltage in millivolts
   */
  public int Vcc;
  /**
   * servo rail voltage in millivolts
   */
  public int Vservo;
  /**
   * power supply status flags (see MAV_POWER_STATUS enum)
   */
  public int flags;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  Vcc = (int)dis.readUnsignedShort()&0x00FFFF;
  Vservo = (int)dis.readUnsignedShort()&0x00FFFF;
  flags = (int)dis.readUnsignedShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+6];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeShort(Vcc&0x00FFFF);
  dos.writeShort(Vservo&0x00FFFF);
  dos.writeShort(flags&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 6);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[12] = crcl;
  buffer[13] = crch;
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_POWER_STATUS : " +   "  Vcc="+Vcc+  "  Vservo="+Vservo+  "  flags="+flags;}
}
