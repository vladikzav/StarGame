package com.star.app.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.WorldRenderer;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.State;


public class GameScreen extends AbstractScreen {
    private GameController gameController;
    private WorldRenderer worldRenderer;
    private State state = State.Running;


    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        Assets.getInstance().loadAssets(ScreenManager.ScreenType.GAME);
        this.gameController = new GameController();
        this.worldRenderer = new WorldRenderer(gameController, batch, this);

    }

    @Override
    public void render(float delta) {
        switch(state){
            case Running:
                worldRenderer.render();
                gameController.update(delta);
                break;
            case Paused:
                worldRenderer.render();
                worldRenderer.renderPause();
                gameController.updatePause(delta);
                break;
        }

    }

    @Override
    public void dispose() {
        gameController.dispose();
    }

    public void setState(State state){
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
