package com.controller_app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.controller_app.helper.Commons;
import com.controller_app.helper_classes.SoundHandler;
import com.controller_app.network.MPClient;
import com.controller_app.screens.KrazyRazyControllerScreen;
import com.controller_app.screens.ConnectionScreen;
import com.controller_app.screens.SettingsScreen;
import com.controller_app.screens.StandbyScreen;
import com.controller_app.screens.VarsomSystemScreen;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

/**
 * <h1>ControllerApp Main Class</h1>
 * This main class holds reference to all other screens,
 * and a switch case to change between them.
 *
 * @author  VarsomGames
 * @version 1.0
 * @since   2015-05-07
 */
public class Main extends Game {
    /**
     * @param settingsScreen This calls to the SettingsScreen
     * @param connectionScreen This calls to the ConnectionScreen
     * @param navigationScreen This calls to the NavigationScreen
     * @param controllerScreen This calls to the ControllerScreen
     * @param standbyScreen This calls to the StandbyScreen
     * @param mpClient This calls to the MPClient to start a new network connection
     */
    private SettingsScreen settingsScreen;
    private ConnectionScreen connectionScreen;
    private VarsomSystemScreen varsomSystemScreen;
    private KrazyRazyControllerScreen krazyRazyControllerScreen;
    private StandbyScreen standbyScreen;
    public MPClient mpClient;
    private Screen activeScreen;

    /**
     * The create-function opens a mpClient, creates the
     * screens and calls on function changeScreen
     * @exception IOException on MPClient
     * @return Nothing.
     */
    @Override
    public void create() {

        try {
            mpClient = new MPClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SoundHandler.load();

        settingsScreen = new SettingsScreen(this);
        connectionScreen = new ConnectionScreen(this, mpClient);
        krazyRazyControllerScreen = new KrazyRazyControllerScreen(this, mpClient);
        varsomSystemScreen = new VarsomSystemScreen(this, mpClient, settingsScreen);
        standbyScreen = new StandbyScreen(this, mpClient);
        mpClient.krazyRazyControllerScreen = krazyRazyControllerScreen;

        changeScreen(Commons.CONNECTION_SCREEN);
    }
    /**
     * The changeScreen-function changes screen based on an index passed
     * on by the program.
     * @param s The index for switching screens
     * @return Nothing.
     */
    public void changeScreen(int s) {
        System.out.println("in changeScreen");
        switch (s) {

            case Commons.CONNECTION_SCREEN:
                System.out.println("change to connectionScreen");
                Gdx.input.setInputProcessor(connectionScreen.getStage());
               // connectionScreen.check = 1;
                setScreen(connectionScreen);
                activeScreen = connectionScreen;
                break;
            case Commons.VARSOM_SYSTEM_SCREEN:
                varsomSystemScreen = new VarsomSystemScreen(this, mpClient, settingsScreen);
                Gdx.input.setInputProcessor(varsomSystemScreen.getStage());
               // connectionScreen.check = 2;
                setScreen(varsomSystemScreen);
                activeScreen = varsomSystemScreen;
                break;
            case Commons.SETTINGS_SCREEN:
                Gdx.input.setInputProcessor(settingsScreen.getStage());
              //  connectionScreen.check = 2;
                setScreen(settingsScreen);
                activeScreen = settingsScreen;
                break;
            case Commons.CRAZY_RAZY_CONTROLLER_SCREEN:
                krazyRazyControllerScreen = new KrazyRazyControllerScreen(this, mpClient);
                Gdx.input.setInputProcessor(krazyRazyControllerScreen.getStage());
               // connectionScreen.check = 2;
                setScreen(krazyRazyControllerScreen);
                activeScreen = krazyRazyControllerScreen;
                break;
            case Commons.STANDBY_SCREEN:
                Gdx.input.setInputProcessor(standbyScreen.getStage());
                setScreen(standbyScreen);
                activeScreen = standbyScreen;
                break;

            default: System.out.println("Error in changeScreen");
        }
    }
    // TODO: Maybe delete this function if not used!
    /**
     * @return The created mpClient
     */
    public MPClient getMpClient(){
        return mpClient;
    }

    /**
     * @return The created client of mpClient
     */
    public Client getClient(){
        return mpClient.client;
    }

    /**
     * @return The created settingsScreen
     */
    public SettingsScreen getSettingsScreen(){
        return settingsScreen;
    }

    /**
     * @return The created ConnectionScreen
     */
    public ConnectionScreen getConnectionScreen(){
        return connectionScreen;
    }

    /**
     * @return The created NavigationScreen
     */
    public VarsomSystemScreen getVarsomSystemScreen(){
        return varsomSystemScreen;
    }

    /**
     * @return The created ControllerScreen
     */
    public KrazyRazyControllerScreen getKrazyRazyControllerScreen(){
        return krazyRazyControllerScreen;
    }

    /**
     * Handles the change of screens on the controller that the server issued
     */
    public void handleController(){
        if(mpClient.getChangeController()) {
            //ControllerScreen and NavigationScreen should not be reused
            if(activeScreen == krazyRazyControllerScreen || activeScreen == varsomSystemScreen)
                activeScreen.dispose();
            changeScreen(mpClient.getActiveScreenIndex());
            mpClient.setChangeController(false);
        }
    }

    public Screen getActiveScreen(){

        return activeScreen;

    }

    @Override
    public void dispose(){
        super.dispose();

    }

}
