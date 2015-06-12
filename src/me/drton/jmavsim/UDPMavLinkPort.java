package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkSchema;
import me.drton.jmavlib.mavlink.MAVLinkStream;
import me.drton.jmavlib.mavlink.MAVLinkMessage;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

/**
 * User: ton Date: 02.12.13 Time: 20:56
 */
public class UDPMavLinkPort extends MAVLinkPort {
    private MAVLinkSchema schema;
    private DatagramChannel channel = null;
    private ByteBuffer rxBuffer = ByteBuffer.allocate(8192);
    private SocketAddress sendAddress;
    private SocketAddress bindPort = null;
    private SocketAddress peerPort;
    private int portAddress;
    private MAVLinkStream stream;
    private boolean debug = false;

    private String[] LOCAL_HOST_TERMS = { "localhost", "127.0.0.1" };

    public UDPMavLinkPort(MAVLinkSchema schema) {
        super(schema);
        this.schema = schema;
        rxBuffer.flip();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setup(int bindPort, String peerAddress, int peerPort) throws UnknownHostException, IOException {
        this.peerPort = new InetSocketAddress(peerAddress, peerPort);
        // If PX4 is running on localhost, we should connect on local host as well.
        for (String term : LOCAL_HOST_TERMS) {
            if (peerAddress.equalsIgnoreCase(term)) {
                this.bindPort = new InetSocketAddress(term, bindPort);
            }
        }
        // Otherwise, we should attempt to find the external IP address and connect over that.
        if (this.bindPort == null) {
            InetAddress localHostExternalIPAddress = getMyHostIPAddress();
            this.bindPort = new InetSocketAddress(localHostExternalIPAddress, bindPort);
        }
        if (debug) {
            System.out.println("peerAddress: " + peerAddress + ", bindAddress: " + this.bindPort.toString());
        }
    }

    /**
     * Searches for the externally-reachable IP address for this machine. Note that if PX4 is running on
     * a private network, this method may or may not work to setup communication.
     *
     * @return the best possible address found.
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException
     */
    private static InetAddress getMyHostIPAddress() throws UnknownHostException, SocketException, IOException {
        InetAddress possibleAddress = null;
        // Look over all the network interfaces for the appropriate interface.
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
           NetworkInterface iface = networkInterfaces.nextElement();
           Enumeration<InetAddress> addresses = iface.getInetAddresses();
           while (addresses.hasMoreElements()) {
               InetAddress address = addresses.nextElement();
               if (!address.isLoopbackAddress()) {
                   if (address.isSiteLocalAddress()) {
                       // probably a good one!
                       return address;
                   } else {
                       // Found a non-loopback address that isn't site local.
                       // Might be link local (private network), but probably better than nothing.
                       possibleAddress = address;
                   }
               }
           }
        }
        // At this point, if we haven't found a better option, we better just take whatever Java thinks is best.
        if (possibleAddress == null) {
            possibleAddress = InetAddress.getLoopbackAddress();
        }
        return possibleAddress;
    }

    public void open() throws IOException {
        channel = DatagramChannel.open();
        channel.socket().bind(bindPort);
        channel.configureBlocking(false);
        channel.connect(peerPort);
        stream = new MAVLinkStream(schema, channel);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public boolean isOpened() {
        return channel != null && channel.isOpen();
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        if (isOpened()) {
            try {
                stream.write(msg);
            } catch (IOException ignored) {
                // Silently ignore this exception, we likely just have nobody on this port yet/already
            }
        }
    }

    @Override
    public void update(long t) {
        while (isOpened()) {
            try {
                MAVLinkMessage msg = stream.read();
                if (msg == null) {
                    break;
                }
                System.out.println("msg.name: " + msg.getMsgName() + ", type: " + msg.getMsgType());
                sendMessage(msg);
            } catch (IOException e) {
                // Silently ignore this exception, we likely just have nobody on this port yet/already
                return;
            }
        }
    }
}
