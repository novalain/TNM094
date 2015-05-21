package com.controller_app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.controller_app.Main;
import com.controller_app.helper.Commons;
import com.controller_app.helper_classes.ScaledScreen;
import com.controller_app.network.Packet;

/**
 * <h1>ControllerApp Settings Screen</h1>
 * This main class holds reference to all other screens,
 * and a switch case to change between them.
 *
 * @author  VarsomGames
 * @version 1.0
 * @since   2015-05-07
 */
public class SettingsScreen extends ScaledScreen {

    /**
     * @param main Calls to the active main class
     * @param skin Skin where textureatlas and fonts are added
     * @param atlas Textureatlas for gathering all textures
     * @param generator Generates fonts
     * @param font Bitmapfont for current font
     * @param table Table collecting all objects on screen and structuring them
     * @param checkVibration 
     */

    private Main main;
    private Skin skin;
    private TextureAtlas atlas;

    private FreeTypeFontGenerator generator;
    private BitmapFont font;

    private Table table;

    private CheckBox checkVibration;
    private CheckBox checkTest;
    private TextButton btnBack;
    private TextField playerName;
    private String strPlayerName = "Player -1";
    private boolean update = true;

    public SettingsScreen(Main main) {
        super();

        this.main = main;

        generateFonts();
        generateSkin();
        generateUI();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.3f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update = updateNameField(update);

        // Sprite renders
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
        Gdx.app.log("in settingsScreen", "in dispose");

    }

    void generateFonts() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.size = 100;
        font = generator.generateFont(parameter);

        try {
            skin.add("default-font", font);
        } catch (Exception e) {
            Gdx.app.log("font", "failed adding font");
        }
        generator.dispose();
    }

    void generateSkin() {
        try {
            atlas = new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas"));
            skin = new Skin();
            skin.addRegions(atlas);
            skin.add("default-font", font, BitmapFont.class);
            skin.load(Gdx.files.internal("uiskin/uiskin.json"));
        } catch (Exception E) {
            System.out.println("Failed at this");
        }
    }

    private void generateUI() {
        skin.getFont("default-font").scale(4f);

        btnBack = new TextButton("Back", skin);
        btnBack.setSize(200, 100);
        btnBack.setPosition(Commons.WORLD_WIDTH - btnBack.getPrefWidth() - 10, Commons.WORLD_HEIGHT - btnBack.getPrefHeight());

        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.vibrate(Commons.VIBRATION_TIME);
                updateConnectionName();
                update = true;
                main.getMpClient().updateNameOnServer(playerName.getText());
                main.changeScreen(Commons.CONNECTION_SCREEN);
            }
        });

        checkTest = new CheckBox("Fun Game", skin);
        checkTest.setChecked(true);
        checkVibration = new CheckBox("Vibration ", skin);
        checkVibration.setChecked(true);

        Label lblTitle = new Label("Settings", skin);

        checkTest.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.graphics.setContinuousRendering(checkTest.isChecked());
            }
        });

        checkVibration.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.graphics.setContinuousRendering(checkVibration.isChecked());
            }
        });

        playerName = new TextField(strPlayerName, skin);

        //Add elements to table

        table = new Table(skin);

        checkTest.getImage().setScale(5);
        checkVibration.getImage().setScale(5);

        table.add(lblTitle).padTop(10).padBottom(40).row();
        table.add(playerName).size(800, 200).padBottom(20).row();
        table.add(checkTest).size(1000, 200).padBottom(20).row();
        table.add(checkVibration).size(1000, 200).padBottom(100).row();

        table.setX(Commons.WORLD_WIDTH / 2 - table.getPrefWidth() / 2);
        table.setY(Commons.WORLD_HEIGHT / 2 - table.getPrefHeight() / 2);

        table.pack();
        stage.addActor(table);

        stage.addActor(btnBack);
    }

   public String getPlayerName(){
       return playerName.getText();
   }

    public boolean updateNameField(boolean b){
        //we only want to update once when we switch to this screen
        if(b) {
            if (main.getClient().getID() != -1) {
                //this clint is connected and was given an automatic name
                strPlayerName = main.getClient().toString();
            }

            playerName.setText(strPlayerName);
        }
        return false;
    }

    public void updateConnectionName(){
        main.getClient().setName(playerName.getText());
        System.out.println("Name: " + playerName.getText());
    }
}
