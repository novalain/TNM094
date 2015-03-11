package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import gameworld.GameRenderer;
import gameworld.GameWorld;

/**
 * Created by Alice on 2015-03-11.
 */
public class GameScreen implements Screen{
    // Class variables
    private GameWorld world;
    private GameRenderer renderer;

    public GameScreen(){
        // creating gameRenderer and GameWorld
        world = new GameWorld();
        renderer = new GameRenderer(world);
    }
    @Override
    public void render(float delta) {
        // connecting GameWorld and GameRenderer and updating them both
        world.update(delta);

        renderer.render();

    }

    @Override
    public void resize(int width, int height) {
        //Gdx.app.log("GameScreen", "resizing in here");
    }

    @Override
    public void show() {
        //Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void hide() {
        //Gdx.app.log("GameScreen", "hide called");
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
    }
}
