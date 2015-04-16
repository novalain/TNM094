package com.controller_app;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class ScaledScreen implements Screen {

    protected Main main;

    protected Stage stage;
    protected TextButton.TextButtonStyle textButtonStyle;
    protected Skin skin;
    protected TextureAtlas buttonAtlas;

    protected OrthographicCamera camera;
    protected Viewport viewport;

    protected BitmapFont font;
    protected Color backgroundColor;

    protected FreeTypeFontGenerator generator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

    // The "super"-constructor

    public ScaledScreen() {

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Commons.WORLD_WIDTH, Commons.WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage();
        font = new BitmapFont();
        skin = new Skin();

        //Gdx.input.setInputProcessor(stage);
        stage.getViewport().setCamera(camera);

    }

    @Override
    public void render(float delta) {
       /*
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    */
    }

    abstract void generateFonts();

    abstract void generateButtons();

    abstract void generateTextButtonStyle();

    protected Stage getStage() {



        return stage;
    }
}
