package com.controller_app.network;

import com.badlogic.gdx.Gdx;
import com.controller_app.screens.ConnectionScreen;
import com.controller_app.screens.ControllerScreen;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class MPClient {
    public Client client;
    public ControllerScreen controllerScreen;
    private BroadcastClient broadcastClient;
    public boolean correctIP;
    private String serverIP;
    private Thread networkThread;


    public MPClient() throws IOException {
        correctIP = false;

        client = new Client();
        register();

        NetworkListener nl = new NetworkListener();
        networkThread = newThread();
        //newThread(networkThread);

        // Initialise variables (not sure if it needed, maybe later)

        nl.init(client, this);
        client.addListener(nl);

        client.start();

        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    // get IP from user input and connects
    public void connectToServer(String ip) {
        // Start a broadcast receiver
        try {
            broadcastClient = new BroadcastClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Gets ip from broadcast
        serverIP = broadcastClient.getServerIP();

        System.out.print("\nIP is " + serverIP + "\n");

        if (serverIP != null && !serverIP.isEmpty()) {

            try {
                client.connect(5000, serverIP.trim(), 54555, 64555);

            } catch (IOException e) {
                e.printStackTrace();
                client.stop();
                // ConnectionScreen.errorMessage(2);
            }
        }
    }

    // Register packets to a kryo
    private void register() {
        Kryo kryo = client.getKryo();
        // Register packets
        kryo.register(Packet.LoginRequest.class);
        kryo.register(Packet.LoginAnswer.class);
        kryo.register(Packet.GamePacket.class);
        kryo.register(Packet.SendGameData.class);
        kryo.register(Packet.ShutDownPacket.class);
        kryo.register(Packet.PauseRequest.class);
        kryo.register(Packet.ExitRequest.class);
        kryo.register(Packet.StandByOrder.class);
        kryo.register(Packet.SendDPadData.class);
    }

    public void sendPacket(boolean send) {

        System.out.println("Sending device data: " + send);
        if(!networkThread.isAlive()){
            Gdx.app.log("SEND PACKETS", "THREAD WASN'T ALIVE");
            if(send) {
                Gdx.app.log("SEND PACKETS", "STARTING THREAD");
                networkThread.start();
            }
            else if (!send) {

            }
        }
        else{
            Gdx.app.log("SEND PACKETS", "THREAD IS ALIVE");
            if(!send) {
                Gdx.app.log("SEND PACKETS", "INTERUPTING THREAD");
                networkThread.interrupt();
            }
            else{
                Gdx.app.log("SEND PACKETS", "RESTARTING THREAD");
                //newThread(networkThread);
                networkThread = newThread();
            }

        }

    }

    //send a boolean for pause state
    public void sendPause(boolean p) {
        Packet.PauseRequest sendState = new Packet.PauseRequest();
        sendState.pause = p;
        client.sendTCP(sendState);
    }

    //send a boolean for pause state
    public void sendExit(boolean p) {
        Packet.ExitRequest sendState = new Packet.ExitRequest();
        sendState.exit = p;
        client.sendTCP(sendState);
        Gdx.app.log("in MPClient", "sent Exit");
    }

    public void sendDPadData(int i) {
        Packet.SendDPadData dp = new Packet.SendDPadData();
        dp.data = i;
        client.sendTCP(dp);
        Gdx.app.log("in MPClient", "sent dPadInfo");
    }

    public void errorHandler() {
        //  ConnectionScreen.errorMessage(1);
    }
    public Thread newThread(){
        Thread nT = new Thread(new Runnable() {

            final int TICKS_PER_SECOND = 30;

            public void run() {

                Gdx.app.log("Thread", "NEW THREAD IS RUNNING");
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(1000 /  TICKS_PER_SECOND );
                        Gdx.app.log("Thread", "DATA IS BEING SENT!!");
                        Packet.GamePacket packet = new Packet.GamePacket();

                        packet.message = controllerScreen.getDrive() + " " + controllerScreen.getReverse() + " " + controllerScreen.getRotation();
                        client.sendUDP(packet);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        return nT;
    }
}