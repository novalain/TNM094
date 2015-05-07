package com.varsom.system.games.car_game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.varsom.system.VarsomSystem;
import com.varsom.system.games.car_game.gameobjects.Car;
import com.varsom.system.games.car_game.tracks.Track;
import com.varsom.system.games.car_game.tracks.Track1;
import com.varsom.system.games.car_game.tracks.Track2;
import com.varsom.system.network.NetworkListener;

import java.util.ArrayList;
import java.util.Collections;


public class GameScreen implements Screen {

    public static int level;

    // For countdown
    private static float countDownTimer = 1.0f;
    private static boolean paused = true;

    // Class variables
    private World world;
    private Box2DDebugRenderer debugRenderer;

    //CAMERA
    private OrthographicCamera camera;
    final float CAMERA_POS_INTERPOLATION = 0.1f;
    final float CAMERA_ROT_INTERPOLATION = 0.015f;

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITY_ITERATIONS = 8,
            POSITION_ITERATIONS = 3;

    private SpriteBatch batch;

    private Track track;

    //    private Pixmap pixmap;
    private Car leaderCar;
    //private Comparator<Car> carComparator;
    private ArrayList<Car> activeCars, sortedCars;

    private Stage stage = new Stage();
    private Table table = new Table();

    //TODO Load files from AssetLoader
    private Skin skin = new Skin(Gdx.files.internal("system/skins/menuSkin.json"),
            new TextureAtlas(Gdx.files.internal("system/skins/menuSkin.pack")));

    private TextButton buttonLeave = new TextButton("Win screen", skin);
    private Label labelPause;
    private String pauseMessage = "Paused";

    //temp
    Label carsTraveled;

    protected VarsomSystem varsomSystem;
    protected int NUMBER_OF_PLAYERS;
    private String diePulse;

    public GameScreen(int level, final VarsomSystem varsomSystem) {
        this.varsomSystem = varsomSystem;
        varsomSystem.setActiveStage(stage);
        this.level = level;
        world = new World(new Vector2(0f, 0f), true);
        debugRenderer = new Box2DDebugRenderer();

        int SCREEN_WIDTH = Gdx.graphics.getWidth();
        int SCREEN_HEIGHT = Gdx.graphics.getHeight();

        NUMBER_OF_PLAYERS = this.varsomSystem.getServer().getConnections().length;

        //TODO THIS IS ONLY TEMPORARY.. DURING DEVELOPMENT
        if (NUMBER_OF_PLAYERS < 2) {
            NUMBER_OF_PLAYERS = 2;
        }

        // Create objects and select level
        switch (level) {
            case 1:
                track = new Track1(world, NUMBER_OF_PLAYERS,varsomSystem);
                break;
            case 2:
                track = new Track2(world, NUMBER_OF_PLAYERS,varsomSystem);
                break;
            default:
                System.out.println("Mega Error");

        }
        varsomSystem.getActiveGame().setGameScreen(this);
        varsomSystem.getMPServer().gameRunning(true);
        leaderCar = track.getCars()[0];
        activeCars = new ArrayList<Car>();
        sortedCars = new ArrayList<Car>();
        addActiveCars();
        createDiePulse();
        //sortCars();

        // Init camera
        //TODO /100 should probably be changed
        camera = new OrthographicCamera(SCREEN_WIDTH/100,SCREEN_HEIGHT/100);
        //TODO camera.position.set(leaderCar.getPointOnTrack(), 0);
        camera.position.set(leaderCar.getPointOnTrack(), 0);
        camera.rotate((float)Math.toDegrees(leaderCar.getRotationTrack())-180);
        camera.zoom = 3.f; // can be used to see the entire track
        camera.update();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // pause menu button
        table.top().left();
        table.add(buttonLeave).size(400, 75).row();
        // connected devices text thingys....
        carsTraveled = new Label("CarDist:\n", skin);
        carsTraveled.setFontScaleX(0.75f);
        carsTraveled.setFontScaleY(0.75f);
        table.add(carsTraveled).size(400, 200).row();

        table.setFillParent(true);
        stage.addActor(table);

        BitmapFont fontType = new BitmapFont();
        fontType.scale(2.f);
        Label.LabelStyle style = new Label.LabelStyle(fontType, Color.WHITE);

        labelPause = new Label(pauseMessage, style);
        labelPause.setPosition(0, 0);

        labelPause.setPosition(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        stage.addActor(labelPause);

        //TODO Denna behövs för att man ska kunna klicka på knappen  men gör att vi inte längre kan gasa
        Gdx.input.setInputProcessor(stage);

        buttonLeave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("clicked", "pressed the Done button.");
                weHaveAWinner();
            }
        });
        //end pause menu button

    }

    //TODO handles count down timer and pause, should the name change or pause be moved?
    private void handleCountDownTimer() {
        paused = NetworkListener.pause;

        countDownTimer -= Gdx.graphics.getDeltaTime();
        float secondsLeft = (int) countDownTimer % 60;

        // Render some kick-ass countdown label
        if (secondsLeft > 0) {
            paused = true;
            //Gdx.app.log("COUNTDOWN: ", (int)secondsLeft + "");
        }
        /*else if(secondsLeft == 0){
            paused = false;
        }*/

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.7f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // If Exit was pressed on a client
        if (NetworkListener.goBack) {
            Gdx.app.log("in GameScreen", "go back to main menu");
            NetworkListener.goBack = false;

            //new clients can join now when the game is over
            varsomSystem.getMPServer().setJoinable(true);

            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(varsomSystem));

            //dispose(); ??
        }

        handleCountDownTimer();
        batch.setProjectionMatrix(camera.combined);
        track.addToRenderBatch(batch, camera);

        //debugRenderer.render(world, camera.combined);

       // Here goes the all the updating / game logic
        if(!paused){

           world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

           String temp = "Standings:\n";
           /*for(Car car : track.getCars()) {
               if(car.isActive() && !camera.frustum.boundsInFrustum(car.getPosition().x,car.getPosition().y,0,0.5f,1f,0.1f)){
                   carLost(car.getID());
               }
               car.update(Gdx.app.getGraphics().getDeltaTime());
               temp += car.getTraveledDistance() + "\n";
           }*/
            for(int i = 0; i < activeCars.size(); i++){
                if(!camera.frustum.boundsInFrustum(activeCars.get(i).getPosition().x,activeCars.get(i).getPosition().y,0,0.5f,1f,0.1f)){
                    carLost(activeCars.get(i).getID(),i);

                    if(i != 0){
                        i--;
                    }
                }
                else {
                    activeCars.get(i).update(Gdx.app.getGraphics().getDeltaTime());
                }
            }
            sortCars();
            temp += sortedCars2String();
            carsTraveled.setText(temp);
            updateCamera();
        }

        //If paused pause menu is displayed, else it is not
        displayPauseMenu(paused);

        stage.act();
        stage.draw();
    }

    private void updateCamera() {
        leaderCar = track.getLeaderCar();
        //leaderCar = track.getCars()[0];
        float newCamPosX = (leaderCar.getPointOnTrack().x - camera.position.x);
        float newCamPosY = (leaderCar.getPointOnTrack().y - camera.position.y);
        Vector2 newPos = new Vector2(camera.position.x + newCamPosX * CAMERA_POS_INTERPOLATION, camera.position.y + newCamPosY * CAMERA_POS_INTERPOLATION);
        //Gdx.app.log("CAMERA","Camera position: " + camera.position);
        if (newPos.x == Float.NaN || newPos.y == Float.NaN) {
            Gdx.app.log("FUUUUDGE", "ERROR");
        }
        camera.position.set(newPos, 0);

        // Convert camera angle from [-180, 180] to [0, 360]
        float camAngle = -getCurrentCameraAngle(camera) + 180;

        float desiredCamRotation = (camAngle - (float) Math.toDegrees(leaderCar.getRotationTrack()) - 90);

        if (desiredCamRotation > 180) {
            desiredCamRotation -= 360;
        } else if (desiredCamRotation < -180) {
            desiredCamRotation += 360;
        }

        camera.rotate(desiredCamRotation * CAMERA_ROT_INTERPOLATION);

        camera.update();

    }

    private float getCurrentCameraAngle(OrthographicCamera cam) {
        return (float) Math.atan2(cam.up.x, cam.up.y) * MathUtils.radiansToDegrees;
    }

    @Override
    public void resize(int width, int height) {
        //Gdx.app.log("GameScreen", "resizing in here");
    }

    @Override
    public void show() {
        Gdx.input.setCatchBackKey(true);
        //Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void hide() {
        //Gdx.app.log("GameScreen", "hide called");
        dispose();
    }

    @Override
    public void pause() {
        //Gdx.app.log("GameScreen", "pause called");
    }

    @Override
    public void resume() {
        //Gdx.app.log("GameScreen", "resume called");
    }

    @Override
    public void dispose() {
        // Leave blank
        System.out.println("GameScreen was disposed");
        varsomSystem.getMPServer().gameRunning(false);
    }

    //makes the pause menu visible if pause == true and invisible if not
    public void displayPauseMenu(boolean pause) {
        //TODO display who paused
        labelPause.setVisible(pause);
        buttonLeave.setVisible(pause);

    }

    public Track getTrack() {
        return track;
    }

    private void carLost(int carID, int activeCarIndex){
        System.out.println("Car # " + carID +" left the screen");
        //varsomSystem.getMPServer().vibrateClient(1000,carID+1);
        varsomSystem.getMPServer().PulseVibrateClient(diePulse,-1,carID+1);
        varsomSystem.getMPServer().gameRunning(false,carID+1);
        track.getCars()[carID].setActive(false);
        activeCars.remove(activeCarIndex);
        //System.out.println("activeCars contains " + activeCars.size() + " items");
        track.getCars()[carID].handleDataFromClients(false,false,0);
        if(activeCars.size() == 1) {
            weHaveAWinner();
        }
    }

    private void weHaveAWinner(){
        ((Game) Gdx.app.getApplicationListener()).setScreen(new WinScreen(varsomSystem,sortedCars2String()));

        varsomSystem.getMPServer().gameRunning(false);
    }

    private void addActiveCars(){
        for(Car car : track.getCars()){
            activeCars.add(car);
            sortedCars.add(car);
        }
        System.out.println("activeCars contains " + activeCars.size() + " items");
    }
    private void sortCars(){
        Collections.sort(sortedCars);
    }

    private String sortedCars2String(){
        String temp = "";
        for(int i = 0; i < sortedCars.size() ; i++){
            /*if(i < varsomSystem.getServer().getConnections().length){
                System.out.println("i = " + i + "No. of connections: " + varsomSystem.getServer().getConnections().length);
                temp += i+1 + ". " + varsomSystem.getServer().getConnections()[sortedCars.get(i).getID()].toString() + "\n";
            }
            else {
                temp += i+1 + ". *NoConnection*\n";
            }*/
            try{
                temp += /*i+1 + ". " +*/ varsomSystem.getServer().getConnections()[sortedCars.get(i).getID()].toString() + "\n";
            }
            catch(Exception e){
                temp += /*i+1 + ". */"*NoConnection*\n";
            }
        }
        return temp;
    }

    private void createDiePulse(){
        diePulse = "0 125 125 125 350 125 125 125 350";
        int paus = 0;
        for(int i = 0 ; i < 200; i++){
            if(i % 10 == 0 ){
                paus++;
            }
            diePulse += " 5 " + paus;
        }
        System.out.println("PULSE: = " + diePulse);
    }


}