package com.controller_app;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuScreen extends ScaledScreen {

    private TextButton button;

    private MPClient mpClient;

    public MenuScreen(Main m, MPClient mpc) {
        super();

        this.main = m;
        mpClient = mpc;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        backgroundColor = new Color(0.7f, 0.0f, 0.0f, 1.0f);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("skins/menuSkin.pack"));
        skin.addRegions(buttonAtlas);

        generateFonts();
        generateTextButtonStyle();
        generateButtons();

        stage.addActor(button);
    }

    @Override
    void generateFonts() {
        parameter.color = Color.WHITE;
        parameter.size = 100;
        font = generator.generateFont(parameter);

        generator.dispose();
    }

    @Override
    void generateTextButtonStyle() {
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("up");
        textButtonStyle.down = skin.getDrawable("down");
    }

    @Override
    void generateButtons() {
        button = new TextButton("Controller", textButtonStyle);
        button.setWidth(800);
        button.setHeight(200);
        button.setPosition(Commons.WORLD_WIDTH / 2 - button.getWidth() / 2, Commons.WORLD_HEIGHT / 2 - button.getHeight() / 2);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Do many great things...

                main.changeScreen(2);

            }
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        mpClient.connectToServer();
    }
}