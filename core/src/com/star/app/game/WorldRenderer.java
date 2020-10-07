package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
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
    private Vector2 tmpVector;
	private GameScreen gameScreen;

    private Camera camera;

    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;
    private Texture texture;




    public WorldRenderer(GameController gc, SpriteBatch batch, final GameScreen gameScreen) {
        this.gc = gc;
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf", BitmapFont.class);
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf", BitmapFont.class);
        this.strBuilder = new StringBuilder();
        this.tmpVector = new Vector2();

        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.SCREEN_WIDTH, ScreenManager.SCREEN_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameBufferRegion.flip(false, true);
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        if (!shaderProgram.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
        }
        this.camera = ScreenManager.getInstance().getCamera();
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

        stage.addActor(gc.getHero().getShop());
        stage.addActor(btnPause);
        gc.setStage(stage);
        skin.dispose();
        Pixmap pixmap = new Pixmap(300, 20, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void levelChange(){
        if(gc.isNewLevel()){
            strBuilder.clear();
            strBuilder.append("Level: " + gc.getLevel());
            font72.draw(batch, strBuilder, ScreenManager.HALF_SCREEN_WIDTH-80, ScreenManager.HALF_SCREEN_HEIGHT);
        }
    }


    public void render() {
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        gc.getBackground().render(batch);
        batch.end();

        camera.position.set(gc.getHero().getPosition().x, gc.getHero().getPosition().y, 0.0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        gc.getHero().render(batch);
        if (gc.getBot().isAlive()) {
            gc.getBot().render(batch);
        }
        gc.getAsteroidController().render(batch);
        gc.getBulletController().render(batch);
        gc.getPowerUpsController().render(batch);
        gc.getParticleController().render(batch);
        gc.getInfoController().render(batch, font32);
        batch.end();
        frameBuffer.end();

        camera.position.set(ScreenManager.HALF_SCREEN_WIDTH, ScreenManager.HALF_SCREEN_HEIGHT, 0.0f);
        camera.update();
        ScreenManager.getInstance().getViewport().apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

//        tmpVector.set(gc.getHero().getPosition());
//        ScreenManager.getInstance().getViewport().project(tmpVector);

        batch.setShader(shaderProgram);
        shaderProgram.setUniformf("px", 0.5f);
        shaderProgram.setUniformf("py", 0.5f);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);
        batch.end();
        batch.setShader(null);

        //Interfaces
        batch.begin();
        gc.getHero().renderGUI(batch, font32);
        batch.draw(texture, 400, ScreenManager.SCREEN_HEIGHT-40, 3 * gc.getTimeShiftProgress(), 20);
        levelChange();
        batch.end();

        gc.getStage().draw();
    }

    public void renderPause(){
        batch.begin();
        gc.getHero().renderGUI(batch, font32);
        strBuilder.clear();
        strBuilder.append("PAUSE");
        font72.draw(batch, strBuilder, ScreenManager.HALF_SCREEN_WIDTH-80, ScreenManager.HALF_SCREEN_HEIGHT);
        batch.end();
    }
}
