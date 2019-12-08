package com.star.app.screen;

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
import com.badlogic.gdx.utils.Align;
import com.star.app.game.Background;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;

public class GameOverScreen extends AbstractScreen{
    private Background background;
    private BitmapFont font72;
    private BitmapFont font24;
    private Stage stage;


    public GameOverScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.background = new Background(null);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font24;
        skin.add("simpleSkin", textButtonStyle);

        Button btnMenu = new TextButton("Return to Menu", textButtonStyle);
        Button btnExitGame = new TextButton("Exit Game", textButtonStyle);
        btnMenu.setPosition(ScreenManager.HALF_SCREEN_WIDTH - btnMenu.getWidth()/2, 210);
        btnExitGame.setPosition(ScreenManager.HALF_SCREEN_WIDTH - btnExitGame.getWidth()/2, 110);

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        stage.addActor(btnMenu);
        stage.addActor(btnExitGame);
        skin.dispose();

    }

    public void update(float dt) {
        background.update(dt);
        stage.act(dt);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.render(batch);
        font72.draw(batch, "GAME OVER", 0, ScreenManager.HALF_SCREEN_HEIGHT, ScreenManager.SCREEN_WIDTH, Align.center, false);
        font72.draw(batch, "SCORE:" + ScreenManager.getInstance().getGame().getScore(),0, ScreenManager.HALF_SCREEN_HEIGHT-100, ScreenManager.SCREEN_WIDTH, Align.center, false);
        batch.end();
        stage.draw();
    }


    @Override
    public void dispose() {
        background.dispose();
    }
}
