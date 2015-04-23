package com.controller_app;


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.controller_app.Packet.*;

/**
 *
 */
public class NetworkListener extends Listener {

    private Client client;
    private ControllerScreen controllerScreen;
    private String message;


    // If you want to send a object, you need to send it with this client variable
    public void init(Client client) {
        this.client = client;
    }

    public void connected(Connection c) {
        client.sendTCP(new LoginRequest());
        System.out.println("You have connected.");

    }

    public void disconnected(Connection c) {
        System.out.println("You have disconnected.");
    }

    public void received(Connection c, Object o) {
        // checks for login answers from server
        if (o instanceof LoginAnswer) {
            Boolean answer = ((LoginAnswer) o).accepted;

            if (answer) {
                String mess = o.toString();
                System.out.println("Message: " + mess);

            } else {
                c.close();
            }

        }
        if (o instanceof Message) {

            //The received message is saved in a string
            message = ((Message) o).message;

            //Writes the message in the log
            System.out.println("MESSAGE: " + message);

        }
        if (o instanceof SendGameData) {

            Boolean start = ((SendGameData) o).start;
            controllerScreen.sendPacket(start);

        }

    }

}
