package com.controller_app.network;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.controller_app.screens.MenuScreen;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 *
 */
public class NetworkListener extends Listener {

    private Client client;
    private MPClient mpClient;


    private boolean start;
    public static boolean connected = false;
    public static boolean standby = false;


    // If you want to send a object, you need to send it with this client variable
    public void init(Client client, MPClient mpClient) {
        this.client = client;
        this.mpClient = mpClient;


    }

    public void connected(Connection c) {
        client.sendTCP(new Packet.LoginRequest());
        System.out.println("You have connected.");

    }

    public void disconnected(Connection c) {
        System.out.println("You have disconnected.");
        mpClient.errorHandler();
    }

    public void received(Connection c, Object o) {
        // checks for login answers from server
        String message = "Recieved a message of type: ";
        if (o instanceof Packet.LoginAnswer) {
            connected = ((Packet.LoginAnswer) o).accepted;
            standby = ((Packet.LoginAnswer) o).standby;

            if (connected) {
                String mess = o.toString();
                System.out.println("in NetworkListener: answer = true");
                //System.out.println("Message: " + mess);
            } else {
                c.close();
            }

        }

        if (o instanceof Packet.SendGameData) {
            message += "SendGameData";
            Gdx.app.log("NETWORK", message);
            start = ((Packet.SendGameData) o).send;
            if(start) {
                new Thread() {
                    public void run() {
                        mpClient.sendPacket(start);
                    }
                }.start();
            }

        }
        if (o instanceof Packet.StandbyOrder) {
            System.out.println("in NetworkListener: standby");

            standby = ((Packet.StandbyOrder) o).standby;

        }

    }

}