package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.screen.GameScreen;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.State;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font24;
    private BitmapFont font72;
    private Stage stage;
    private StringBuilder strBuilder;
    private GameScreen gameScreen;




    public WorldRenderer(GameController gc, SpriteBatch batch, final GameScreen gameScreen) {
        this.gc = gc;
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf", BitmapFont.class);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf", BitmapFont.class);
        this.strBuilder = new StringBuilder();
        this.gameScreen = gameScreen;
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font24;
        skin.add("simpleSkin", textButtonStyle);

        Button btnPause = new TextButton("Pause", textButtonStyle);

        btnPause.setPosition(ScreenManager.SCREEN_WIDTH - btnPause.getWidth() - 5, ScreenManager.SCREEN_HEIGHT - btnPause.getHeight() - 5);


        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(gameScreen.getState().equals(State.Running))
                    gameScreen.setState(State.Paused);
                else if(gameScreen.getState().equals(State.Paused))
                    gameScreen.setState(State.Running);
            }
        });

        stage.addActor(btnPause);
        gc.setStage(stage);
        skin.dispose();
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gc.getBackground().render(batch);
        gc.getHero().render(batch);
        gc.getAsteroidController().render(batch);
        gc.getBulletController().render(batch);
        gc.getPowerUpsController().render(batch);
        gc.getParticleController().render(batch);
        gc.getHero().renderGUI(batch, font32);
        batch.end();
        gc.getStage().draw();
    }

    public void renderPause(){
        batch.begin();
        strBuilder.clear();
        strBuilder.append("PAUSE");
        font72.draw(batch, strBuilder, ScreenManager.HALF_SCREEN_WIDTH-80, ScreenManager.HALF_SCREEN_HEIGHT);
        batch.end();
    }
}
