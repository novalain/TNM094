package com.varsom.system.games.car_game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.varsom.system.Commons;
import com.varsom.system.VarsomSystem;
import com.varsom.system.games.car_game.gameobjects.Car;
import com.varsom.system.network.NetworkListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

import java.util.Comparator;

public class WinScreen extends ScaledScreen {

    public class CarComparator implements Comparator<Car>{
        @Override
        public int compare(Car c1, Car c2) {
            if (c1.getTraveledDistance() < c2.getTraveledDistance()) {
                return -1;
            } else if (c1.getTraveledDistance() == c2.getTraveledDistance()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private Table table = new Table();

    protected VarsomSystem varsomSystem;

    private ArrayList<String> carOrder;
    /*
    protected Array<Car> carList = new Array<Car>(8);
    private CarComparator carComparator = new CarComparator();
*/

    //TODO Load files from AssetLoader

    private Skin skin = new Skin(Gdx.files.internal("car_game_assets/skins/menuSkin.json"),
            new TextureAtlas(Gdx.files.internal("car_game_assets/skins/menuSkin.pack")));

    private TextButton btnOK;
    private Label result;
    private Label score;

    private String playerScores;

    public WinScreen(VarsomSystem varsomSystem, String names) {
        this.varsomSystem = varsomSystem;
        carOrder = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(names, "\n");
        while(st.hasMoreTokens()){
            carOrder.add(st.nextToken());
        }

        //Switch screen on the controller to NavigationScreen
        varsomSystem.getMPServer().changeScreen(Commons.NAVIGATION_SCREEN);
    }

    @Override
    public void show() {
        btnOK = new TextButton("OK", skin);
        btnOK.setPosition(Commons.WORLD_WIDTH / 2 - btnOK.getWidth() / 2, Commons.WORLD_HEIGHT * 0.2f);

        btnOK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("clicked", "pressed the OK button.");
                varsomSystem.getMPServer().setJoinable(true);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(varsomSystem));
            }
        });

        BitmapFont fontType = new BitmapFont();
        fontType.scale(2.f);
        Label.LabelStyle style = new Label.LabelStyle(fontType, Color.WHITE);

        //label that shows all connected players
        //playerScores = ": Name : Time/Score/Dist : Knockouts :\n";
        //result = new Label(playerScores, style);
        result = new Label(carOrder.get(0) + " is VICTORIOUS!!", style);
        result.setPosition(Commons.WORLD_WIDTH / 2 - result.getWidth() / 2, Commons.WORLD_HEIGHT * 0.8f - result.getHeight());
        score = new Label(playerScores, style);
        score.setPosition(Commons.WORLD_WIDTH / 2 - score.getWidth() / 2, Commons.WORLD_HEIGHT * 0.6f - score.getHeight());

        stage.addActor(result);
        stage.addActor(score);
        stage.addActor(btnOK);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(122 / 255.0f, 209 / 255.0f, 255 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleDpad();
        handleScore();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void handleDpad() {

        if (NetworkListener.dPadSelect) {
            Gdx.app.log("clicked", "pressed the OK button.");
            varsomSystem.getMPServer().setJoinable(true);
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu(varsomSystem));

            NetworkListener.dPadSelect = false;
        }
    }

    private void handleScore(){

        /*playerScores = ": Name : Time/Score/Dist : Knockouts :\n";

        for(String car : carOrder) {
            Gdx.app.log("handleScore " + carOrder., varsomSystem.getServer().getConnections()[carList.get(i).getID()].toString());
            //Ranking order
            playerScores += varsomSystem.getServer().getConnections()[carList.get(i).getID()].toString() + " : ";
            //Points or time
            playerScores += carList.get(i).getTraveledDistance() + " : ";
            //Knockouts
            playerScores += "- : \n";
        }

        score.setText(playerScores);*/

    }
}

/*

public class WinScreen extends ScaledScreen {

    public void render(){

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for(Car car : carList) {
            //Ranking order
            players += varsomSystem.getServer().getConnections()[car.getID()].toString() + " : ";
            //Points or time
            players += car.getTraveledDistance() + " : ";
            //Knockouts
            players += "- : ";
            //OK button

        }

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
*/