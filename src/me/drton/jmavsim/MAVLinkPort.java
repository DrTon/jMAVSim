package me.drton.jmavsim;

import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public interface MAVLinkPort {
    void close() throws IOException;

    boolean isOpened();

    boolean hasNextMessage() throws IOException;

    MAVLinkMessage getNextMessage() throws IOException;

    void sendMessage(MAVLinkMessage msg) throws IOException;
}
