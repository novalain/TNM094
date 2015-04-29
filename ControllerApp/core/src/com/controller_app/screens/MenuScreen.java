package com.controller_app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.controller_app.Main;

import com.controller_app.network.MPClient;

import com.controller_app.helper.Commons;
import com.controller_app.network.NetworkListener;

public class MenuScreen extends ScaledScreen {

    private TextButton buttonController;
    private TextButton buttonExit;
    private TextField textField;

    private Table table;

    private TextureAtlas atlas;

    private Texture logo;
    private MPClient mpClient;

    private Main main;
    private Skin skin;
    private BitmapFont font;

    private FreeTypeFontGenerator generator;
    private SpriteBatch spriteBatch;
    public int check;

    public MenuScreen(Main m, MPClient mpc) {
        super();

        spriteBatch = new SpriteBatch();

        this.main = m;
        mpClient = mpc;

        //logo
        logo = new Texture(Gdx.files.internal("images/logo.png"));

        // font generator
        generateFonts();

        try {
            atlas = new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas"));
            skin = new Skin();
            skin.addRegions(atlas);
            skin.add("default-font", font, BitmapFont.class);
            skin.load(Gdx.files.internal("uiskin/uiskin.json"));
        } catch (Exception E) {
            Gdx.app.log("font", "failed reading it");
        }

        generateUI();

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

    void generateTextButtonStyle() {
/*        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("up");
        textButtonStyle.down = skin.getDrawable("down");

        textButtonStyle.up = skin.getDrawable("up");
        textButtonStyle.down = skin.getDrawable("down"); */
    }

    void generateUI() {

        skin.getFont("default-font").scale(4f);
        table = new Table(skin);

        Image image = new Image(logo);
        buttonController = new TextButton("Connect Controller", skin);
        buttonExit = new TextButton("Exit", skin);
        textField = new TextField("", skin);

        buttonController.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("in MenuScreen", "pressed controller");
                //main.changeScreen(2);
                connect();
            }
        });

        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                dispose();
            }
        });

        // table.debug();
        table.add(image).padTop(10).padBottom(40).row();
        table.add(textField).size(800, 200).padBottom(20).row();
        table.add(buttonController).size(800, 200).padBottom(100).row();
        table.add(buttonExit).size(800, 200).row();

        table.setX(Commons.WORLD_WIDTH / 2 - table.getPrefWidth() / 2);
        table.setY(Commons.WORLD_HEIGHT / 2 - table.getPrefHeight() / 2);

        table.pack();
        stage.addActor(table);

        System.out.println("image: " + table.getPrefWidth() + " , " + table.getPrefHeight());
    }

    public void errorMessage(int s){
        if(check == 2) {
            main.changeScreen(1);
        }
        switch(s){
            case 1:
            new Dialog("Error", skin) {
                {
                    text("It's seems that your connection sucks");
                    button("Ok");
                }

                @Override
                protected void result(final Object object) {

                }

            }.show(stage);
                break;

            case 2:
                main.changeScreen(1);
                new Dialog("Error", skin) {
                    {
                        text("Please enter a correct IP");
                        button("Ok");
                    }

                    @Override
                    protected void result(final Object object) {

                    }

                }.show(stage);
                break;
        }


    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Check if we have connected we should change to the controllerScreen
        if(NetworkListener.connected)
            main.changeScreen(2);

        // Sprite renders
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        spriteBatch.end();

        stage.act(delta);
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

    }

    // Connect to server
    public void connect() {
        mpClient.connectToServer(textField.getText());
    }

    // Disconnect from server
    public void disconnect() {
        mpClient.client.stop();
        NetworkListener.connected = false;
    }
}