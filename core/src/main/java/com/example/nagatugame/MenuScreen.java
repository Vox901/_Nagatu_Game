package com.example.nagatugame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MenuScreen implements Screen {
    private final NagatuGame game;
    private final Stage stage;
    private final ShapeRenderer sr;

    public MenuScreen(NagatuGame game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, new OrthographicCamera()));
        this.sr = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontLarge, Constants.C_GOLD);
        Label title = new Label("NAGATU", titleStyle);

        Label.LabelStyle subStyle = new Label.LabelStyle(game.font, Constants.C_CLAY);
        Label subtitle = new Label("ОЖЕРЕЛЬЕ СУДЬБЫ", subStyle);

        TextButton.TextButtonStyle btnStyle = UIUtils.createButtonStyle(game.font, Constants.UI_BUTTON_NORMAL);

        TextButton btnPlay = new TextButton("▶ ИГРАТЬ", btnStyle);
        btnPlay.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, 1));
            }
        });

        TextButton btnStats = new TextButton("📊 СТАТИСТИКА", btnStyle);
        btnStats.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StatsScreen(game));
            }
        });

        TextButton btnExit = new TextButton("✕ ВЫХОД", btnStyle);
        btnExit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(title).padBottom(5).row();
        table.add(subtitle).padBottom(40).row();
        table.add(btnPlay).width(320).height(75).pad(10).row();
        table.add(btnStats).width(320).height(75).pad(10).row();
        table.add(btnExit).width(320).height(75).pad(10);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.C_LAPIS.r, Constants.C_LAPIS.g, Constants.C_LAPIS.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();
        stage.act(delta);
        stage.draw();
    }

    private void drawBackground() {
        sr.setProjectionMatrix(stage.getViewport().getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float cx = Constants.WIDTH / 2;
        sr.setColor(Constants.C_STONE);
        sr.triangle(cx, 100, cx - 200, 250, cx + 200, 250);
        sr.rect(cx - 140, 250, 280, 50);
        sr.triangle(cx, 300, cx - 120, 380, cx + 120, 380);
        sr.setColor(Constants.C_GOLD);
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * Math.PI / 4);
            float x = cx + (float)Math.cos(angle) * 350;
            float y = Constants.HEIGHT / 2 + (float)Math.sin(angle) * 250;
            sr.circle(x, y, 6);
        }
        sr.end();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); sr.dispose(); }
}
