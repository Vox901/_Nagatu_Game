package com.example.nagatugame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen implements Screen {
    private final NagatuGame game;
    private final Screen previousScreen;
    private Stage stage;

    public PauseScreen(NagatuGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        Label title = new Label("ПАУЗА", new Label.LabelStyle(game.fontLarge, Constants.C_GOLD));
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.font;
        btnStyle.fontColor = Constants.C_GOLD;

        TextButton btnResume = new TextButton("▶ ПРОДОЛЖИТЬ", btnStyle);
        btnResume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(previousScreen);
            }
        });

        TextButton btnMenu = new TextButton("🏠 В МЕНЮ", btnStyle);
        btnMenu.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(title).padBottom(50).row();
        table.add(btnResume).width(250).height(60).pad(10).row();
        table.add(btnMenu).width(250).height(60).pad(10);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); }
}
