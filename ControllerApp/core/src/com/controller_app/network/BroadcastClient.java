package com.controller_app.network;

/**
 * Created by christoffer on 2015-04-28.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class BroadcastClient {

    public String IP;

    public BroadcastClient() throws IOException {

        MulticastSocket socket = new MulticastSocket(4446);
        //InetAddress address = InetAddress.getByName("230.0.0.1");
        //socket.joinGroup(address);

        DatagramPacket packet = null;

        // get a few quotes
        while (packet == null) {
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            System.out.println("CONTROLLER TRYING TO CONNECT TO " + IP);

            System.out.println("packet getData()" + packet.getData());
            System.out.println("packet getLength()" + packet.getLength());

            IP = new String(packet.getData(), 0, packet.getLength());

        }

        //socket.leaveGroup(address);
        socket.close();
    }

    public String getServerIP() { return IP; }

}