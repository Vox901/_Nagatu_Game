package com.example.nagatugame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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

public class GameOverScreen implements Screen {
    private final NagatuGame game;
    private final int finalScore;
    private final int level;
    private final int shots;
    private final int maxCombo;
    private final Stage stage;
    private final ShapeRenderer sr;

    public GameOverScreen(NagatuGame game, int score, int level, int shots, int combo) {
        this.game = game;
        this.finalScore = score;
        this.level = level;
        this.shots = shots;
        this.maxCombo = combo;
        this.stage = new Stage(new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, new OrthographicCamera()));
        this.sr = new ShapeRenderer();

        Gdx.input.setInputProcessor(stage);

        // Record results
        game.recordGameEnd(shots, combo);
        game.updateHighScore(score);

        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontLarge, Constants.C_CLAY);
        Label title = new Label("ИГРА ОКОНЧЕНА", titleStyle);
        table.add(title).padBottom(40).row();

        Table statsTable = new Table();
        String[] stats = {
            "Счет: " + finalScore,
            "Уровень: " + level,
            "Выстрелов: " + shots,
            "Макс. комбо: x" + maxCombo
        };
        for (String stat : stats) {
            Label lbl = new Label(stat, new Label.LabelStyle(game.font, Color.WHITE));
            statsTable.add(lbl).pad(6).row();
        }
        table.add(statsTable).padBottom(30).row();

        if (finalScore >= game.getHighScore() && finalScore > 0) {
            Label newRecord = new Label("🏆 НОВЫЙ РЕКОРД!", new Label.LabelStyle(game.font, Constants.C_GOLD));
            table.add(newRecord).padBottom(35).row();
        }

        TextButton.TextButtonStyle btnStyle = UIUtils.createButtonStyle(game.font, Constants.UI_BUTTON_NORMAL);

        TextButton btnRetry = new TextButton("🔄 ПОВТОРИТЬ", btnStyle);
        btnRetry.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, level));
            }
        });

        TextButton btnMenu = new TextButton("🏠 В МЕНЮ", btnStyle);
        btnMenu.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(btnRetry).width(350).height(80).pad(15).row();
        table.add(btnMenu).width(350).height(80).pad(15);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.12f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); sr.dispose(); }
}
