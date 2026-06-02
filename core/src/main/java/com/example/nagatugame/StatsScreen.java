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

public class StatsScreen implements Screen {
    private final NagatuGame game;
    private final Stage stage;
    private final ShapeRenderer sr;

    public StatsScreen(NagatuGame game) {
        this.game = game;
        this.stage = new Stage(new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, new OrthographicCamera()));
        this.sr = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        Label title = new Label("СТАТИСТИКА", new Label.LabelStyle(game.fontLarge, Constants.C_GOLD));
        table.add(title).padBottom(30).row();

        Table statsTable = new Table();
        String[] stats = {
            "🏆 Рекорд: " + game.getHighScore(),
            "🎮 Игр сыграно: " + game.getTotalGames(),
            "🎯 Всего выстрелов: " + game.getTotalShots(),
            "💫 Макс. комбо: x" + game.getMaxCombo()
        };
        for (String stat : stats) {
            Label lbl = new Label(stat, new Label.LabelStyle(game.font, Constants.C_WHITE));
            statsTable.add(lbl).left().pad(8).row();
        }
        table.add(statsTable).padBottom(40).row();

        TextButton.TextButtonStyle btnStyle = UIUtils.createButtonStyle(game.font, Constants.UI_BUTTON_NORMAL);

        TextButton btnBack = new TextButton("← НАЗАД", btnStyle);
        btnBack.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(btnBack).width(280).height(75);
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
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.setColor(Constants.C_STONE);
        sr.rect(Constants.WIDTH/2 - 280, Constants.HEIGHT/2 - 220, 560, 440);
        sr.setColor(Constants.C_GOLD);
        sr.rect(Constants.WIDTH/2 - 275, Constants.HEIGHT/2 - 215, 550, 430);
        sr.setColor(Constants.C_LAPIS);
        sr.rect(Constants.WIDTH/2 - 270, Constants.HEIGHT/2 - 210, 540, 420);
        sr.end();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); sr.dispose(); }
}
