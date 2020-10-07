package com.star.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.Background;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;
import com.star.app.screen.utils.TextInputListener;

public class OptionsScreen extends AbstractScreen{

    public OptionsScreen(SpriteBatch batch) {
        super(batch);
    }

    private Background background;
    private BitmapFont font24;
    private Stage stage;


    @Override
    public void show() {
        this.background = new Background(null);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        //Button Style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font24;
        skin.add("simpleSkin", textButtonStyle);

        //Buttons
        Button btnForward = new TextButton("Forward", textButtonStyle);
        Button btnBackward = new TextButton("Backward", textButtonStyle);
        Button btnLeft = new TextButton("Left", textButtonStyle);
        Button btnRight = new TextButton("Right", textButtonStyle);
        Button btnFire = new TextButton("Fire", textButtonStyle);
        Button btnMenu = new TextButton("Save and back\n to Menu", textButtonStyle);
        Button btnDef = new TextButton("Get Default Settings", textButtonStyle);

        //Label Style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.background = skin.getDrawable("simpleButton");
        labelStyle.font = font24;

        //Labels
        final Label labelForward = new Label(Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_FORWARD"))), labelStyle);
        labelForward.setAlignment(Align.center);
        final TextInputListener listenerForward;
        listenerForward = new TextInputListener(labelForward);

        final Label labelBackward = new Label(Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_BACKWARD"))), labelStyle);
        labelBackward.setAlignment(Align.center);
        final TextInputListener listenerBackward;
        listenerBackward = new TextInputListener(labelBackward);

        final Label labelLeft = new Label(Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_LEFT"))), labelStyle);
        labelLeft.setAlignment(Align.center);
        final TextInputListener listenerLeft;
        listenerLeft = new TextInputListener(labelLeft);

        final Label labelRight = new Label(Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_RIGHT"))), labelStyle);
        labelRight.setAlignment(Align.center);
        final TextInputListener listenerRight;
        listenerRight = new TextInputListener(labelRight);

        final Label labelFire = new Label(Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_FIRE"))), labelStyle);
        labelFire.setAlignment(Align.center);
        final TextInputListener listenerFire;
        listenerFire = new TextInputListener(labelFire);

        //Scroll Table
        Table scrollTable = new Table();

        scrollTable.setSize(Gdx.graphics.getHeight()/2, Gdx.graphics.getHeight()/2);
        scrollTable.setPosition(Gdx.graphics.getWidth()/2-scrollTable.getWidth()/2,
                Gdx.graphics.getHeight()/2-scrollTable.getHeight()/2);
        int CollumnWidth = (int) (scrollTable.getWidth()/8*5);
        int pad = 5;
        int height = 60;

        scrollTable.add(btnForward).pad(pad).width(CollumnWidth).height(height);
        scrollTable.add(labelForward).pad(pad).width(CollumnWidth).height(height);
        scrollTable.row();
        scrollTable.add(btnBackward).pad(pad).width(CollumnWidth).height(height);
        scrollTable.add(labelBackward).pad(pad).width(CollumnWidth).height(height);
        scrollTable.row();
        scrollTable.add(btnLeft).pad(pad).width(CollumnWidth).height(height);
        scrollTable.add(labelLeft).pad(pad).width(CollumnWidth).height(height);
        scrollTable.row();
        scrollTable.add(btnRight).pad(pad).width(CollumnWidth).height(height);
        scrollTable.add(labelRight).pad(pad).width(CollumnWidth).height(height);
        scrollTable.row();
        scrollTable.add(btnFire).pad(pad).width(CollumnWidth).height(height);
        scrollTable.add(labelFire).pad(pad).width(CollumnWidth).height(height);
        scrollTable.row();
        scrollTable.add(btnMenu).pad(50,pad,pad,pad).width(CollumnWidth).height(height);
        scrollTable.add(btnDef).pad(50,pad,pad,pad).width(CollumnWidth).height(height);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);

        //Main Table
        Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();
        table.row();

        //Button Listenners
        btnForward.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.getTextInput(listenerForward, "Enter Button",
                        "", Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_FORWARD"))));
            }
        });

        btnBackward.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.getTextInput(listenerBackward, "Enter Button",
                        "", Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_BACKWARD"))));
            }
        });

        btnLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.getTextInput(listenerLeft, "Enter Button",
                        "", Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_LEFT"))));
            }
        });

        btnRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.getTextInput(listenerRight, "Enter Button",
                        "", Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_RIGHT"))));
            }
        });

        btnFire.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.getTextInput(listenerFire, "Enter Button",
                        "", Input.Keys.toString(Integer.parseInt(OptionsUtils.loadProperties().getProperty( "PLAYER1_FIRE"))));
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
                OptionsUtils.saveProperties(
                        Input.Keys.valueOf(String.valueOf(labelForward.getText())),
                        Input.Keys.valueOf(String.valueOf(labelBackward.getText())),
                        Input.Keys.valueOf(String.valueOf(labelRight.getText())),
                        Input.Keys.valueOf(String.valueOf(labelLeft.getText())),
                        Input.Keys.valueOf(String.valueOf(labelFire.getText()))
                );
            }
        });

        btnDef.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
                if (OptionsUtils.isOptionsExists()) {
                    Gdx.files.local("options.properties").delete();
                    OptionsUtils.createDefaultProperties();
                }
            }
        });


        //Stage init
        stage.addActor(table);
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
        batch.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}

