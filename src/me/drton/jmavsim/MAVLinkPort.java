package me.drton.jmavsim;

import java.io.IOException;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public abstract class MAVLinkPort extends MAVLinkNode {
    public abstract void close() throws IOException;

    public abstract boolean isOpened();

    public abstract void update(long t);
}
