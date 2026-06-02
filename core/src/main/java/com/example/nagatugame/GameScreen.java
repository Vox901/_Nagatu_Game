package com.example.nagatugame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

public class GameScreen implements Screen, InputProcessor {
    private final NagatuGame game;
    private final int levelNumber;
    private final ShapeRenderer sr;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Stage uiStage;

    private Path path;
    private final ArrayList<Ball> balls;
    private final ArrayList<Particle> particles;
    private Cannon cannon;
    private Ball flyingBall;

    private float score;
    private int combo;
    private float comboTimer;
    private boolean gameOver;
    private boolean paused;
    private int shotsFired;
    private float ballSpeed;
    private float goalPosition;

    private float shakeIntensity = 0;
    private float shakeDuration = 0;
    private float dangerTimer = 0;

    private Table pauseTable;
    private TextButton btnPause;

    private float touchHoldTime = 0;
    private boolean touchSwapped = false;
    private boolean isTouching = false;

    public GameScreen(NagatuGame game, int level) {
        this.game = game;
        this.levelNumber = level;
        this.sr = new ShapeRenderer();
        this.balls = new ArrayList<>();
        this.particles = new ArrayList<>();

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, camera);
        this.uiStage = new Stage(new ExtendViewport(Constants.WIDTH, Constants.HEIGHT, new OrthographicCamera()));

        initGame();
        buildUI();
    }

    private void initGame() {
        path = LevelGenerator.generateLevel(levelNumber);
        cannon = new Cannon(Constants.WIDTH / 2, Constants.HEIGHT / 2);
        spawnInitialBalls();
        score = 0;
        combo = 0;
        comboTimer = 0;
        gameOver = false;
        paused = false;
        shotsFired = 0;
        ballSpeed = Constants.BALL_SPEED_START + (levelNumber - 1) * 6;
        goalPosition = path.getTotalLength() - 80;

        validateCannonColors();
    }

    private void buildUI() {
        TextButton.TextButtonStyle style = UIUtils.createButtonStyle(game.fontSmall, Constants.UI_BUTTON_NORMAL);
        btnPause = new TextButton("|| ПАУЗА", style);
        btnPause.setPosition(Constants.WIDTH - 150, 20);
        btnPause.setSize(130, 50);
        btnPause.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
        uiStage.addActor(btnPause);

        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);

        TextButton.TextButtonStyle bigStyle = UIUtils.createButtonStyle(game.font, Constants.UI_BUTTON_NORMAL);
        TextButton btnResume = new TextButton("ПРОДОЛЖИТЬ", bigStyle);
        btnResume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        TextButton btnQuit = new TextButton("В МЕНЮ", bigStyle);
        btnQuit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        pauseTable.add(btnResume).width(350).height(80).pad(15).row();
        pauseTable.add(btnQuit).width(350).height(80).pad(15);
        uiStage.addActor(pauseTable);
    }

    private void togglePause() {
        paused = !paused;
        pauseTable.setVisible(paused);
        btnPause.setVisible(!paused);
    }

    private void spawnInitialBalls() {
        balls.clear();
        int ballCount = 20 + levelNumber * 3;
        for (int i = 0; i < ballCount; i++) {
            Color color = Constants.BALL_COLORS[MathUtils.random(Constants.BALL_COLORS.length - 1)];
            Ball ball = new Ball(color);
            ball.pathPosition = -i * Constants.BALL_SPACING;
            balls.add(ball);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.C_LAPIS.r, Constants.C_LAPIS.g, Constants.C_LAPIS.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused && !gameOver) {
            update(delta);
        }

        updateShake(delta);
        viewport.apply();
        sr.setProjectionMatrix(camera.combined);
        game.batch.setProjectionMatrix(camera.combined);

        draw();

        // Darken screen if paused
        if (paused) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0, 0, 0, 0.7f);
            sr.rect(0, 0, Constants.WIDTH, Constants.HEIGHT);
            sr.end();
        }

        uiStage.getViewport().apply();
        uiStage.act(delta);
        uiStage.draw();
    }

    private void updateShake(float delta) {
        if (shakeDuration > 0) {
            shakeDuration -= delta;
            float currentShake = shakeIntensity * (shakeDuration / 0.3f);
            camera.position.x = Constants.WIDTH / 2 + MathUtils.random(-currentShake, currentShake);
            camera.position.y = Constants.HEIGHT / 2 + MathUtils.random(-currentShake, currentShake);
            camera.update();
        } else {
            camera.position.set(Constants.WIDTH / 2, Constants.HEIGHT / 2, 0);
            camera.update();
            shakeIntensity = 0;
        }
    }

    private void shake(float intensity) {
        this.shakeIntensity = Math.max(this.shakeIntensity, intensity);
        this.shakeDuration = 0.3f;
    }

    private void update(float delta) {
        if (path == null) return;

        // Aim tracking
        if (!Gdx.input.isTouched()) {
            Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            cannon.aimAt(mouse.x, mouse.y);
        } else if (isTouching && !touchSwapped) {
            Vector2 touch = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (touch.dst(cannon.position) < 80) {
                touchHoldTime += delta;
                if (touchHoldTime > 0.4f) {
                    cannon.swapColors();
                    touchSwapped = true;
                    shake(8f);
                }
            }
        }

        cannon.update(delta);
        updateBallsLogic(delta);
        if (flyingBall != null) updateFlyingBall(delta);
        updateParticles(delta);

        if (comboTimer > 0) {
            comboTimer -= delta;
            if (comboTimer <= 0) combo = 0;
        }

        dangerTimer += delta * 5;
    }

    private void updateBallsLogic(float delta) {
        if (balls.isEmpty() && !gameOver) {
             game.setScreen(new GameScreen(game, levelNumber + 1));
             return;
        }

        balls.sort((b1, b2) -> Float.compare(b2.pathPosition, b1.pathPosition));

        if (!balls.isEmpty()) {
            balls.get(0).pathPosition += ballSpeed * delta;

            for (int i = 1; i < balls.size(); i++) {
                Ball current = balls.get(i);
                Ball front = balls.get(i - 1);
                float dist = front.pathPosition - current.pathPosition;

                if (dist < Constants.BALL_SPACING) {
                    current.pathPosition = front.pathPosition - Constants.BALL_SPACING;
                } else if (dist > Constants.BALL_SPACING && current.equalsColor(front)) {
                    float attractionSpeed = ballSpeed * 12 * (dist / Constants.BALL_SPACING);
                    current.pathPosition += attractionSpeed * delta;
                    if (current.pathPosition > front.pathPosition - Constants.BALL_SPACING) {
                        current.pathPosition = front.pathPosition - Constants.BALL_SPACING;
                        checkMatches();
                    }
                } else {
                    if (current.pathPosition < 0 || dist < Constants.BALL_SPACING + 10) {
                        current.pathPosition += ballSpeed * delta;
                    }
                }
            }

            if (balls.get(0).pathPosition >= goalPosition) {
                gameOver = true;
                game.setScreen(new GameOverScreen(game, (int)score, levelNumber, shotsFired, combo));
            }
        }
    }

    private void updateFlyingBall(float delta) {
        flyingBall.update(delta);

        boolean hit = false;
        if (flyingBall.position.dst(cannon.position) > 40) {
            for (int i = 0; i < balls.size(); i++) {
                Ball b = balls.get(i);
                if (b.pathPosition < 0) continue;

                if (flyingBall.position.dst(b.position) < Constants.BALL_SPACING * 0.95f) {
                    insertBallAtPosition(flyingBall, b.pathPosition - Constants.BALL_SPACING / 2.1f);
                    hit = true;
                    break;
                }
            }
        }

        if (hit) {
            flyingBall = null;
            checkMatches();
            validateCannonColors();
        } else if (flyingBall != null && (flyingBall.position.x < -300 || flyingBall.position.x > Constants.WIDTH + 300 ||
            flyingBall.position.y < -300 || flyingBall.position.y > Constants.HEIGHT + 300)) {
            flyingBall = null;
        }
    }

    private void updateParticles(float delta) {
        particles.removeIf(p -> !p.update(delta));
    }

    private void insertBallAtPosition(Ball ball, float position) {
        ball.isFlying = false;
        ball.pathPosition = position;
        ball.alive = true;

        int idx = 0;
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).pathPosition < position) {
                idx = i;
                break;
            }
            idx++;
        }
        balls.add(idx, ball);

        for (int i = idx + 1; i < balls.size(); i++) {
            Ball prev = balls.get(i-1);
            Ball curr = balls.get(i);
            if (prev.pathPosition - curr.pathPosition < Constants.BALL_SPACING) {
                curr.pathPosition = prev.pathPosition - Constants.BALL_SPACING;
            }
        }
    }

    private void checkMatches() {
        boolean groupFound;
        do {
            groupFound = false;
            if (balls.size() < Constants.COMBO_MIN) break;

            int start = -1;
            int count = 1;
            for (int i = 0; i < balls.size() - 1; i++) {
                if (balls.get(i).equalsColor(balls.get(i+1)) &&
                    (balls.get(i).pathPosition - balls.get(i+1).pathPosition) < Constants.BALL_SPACING + 5) {
                    if (start == -1) start = i;
                    count++;
                } else {
                    if (count >= Constants.COMBO_MIN) break;
                    start = -1;
                    count = 1;
                }
            }

            if (count >= Constants.COMBO_MIN) {
                for (int i = 0; i < count; i++) {
                    Ball b = balls.get(start);
                    createExplosion(b.position.x, b.position.y, b.color);
                    balls.remove(start);
                }
                combo++;
                comboTimer = 3.0f;
                score += count * 40 * combo;
                shake(10f * combo);
                groupFound = true;
            }
        } while (groupFound);
    }

    private void createExplosion(float x, float y, Color color) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    private void validateCannonColors() {
        if (balls.isEmpty()) return;

        ArrayList<Color> availableColors = new ArrayList<>();
        for (Ball b : balls) {
            if (!availableColors.contains(b.color)) availableColors.add(b.color);
        }

        if (availableColors.isEmpty()) return;

        if (!availableColors.contains(cannon.currentBallColor)) {
            cannon.currentBallColor = availableColors.get(MathUtils.random(availableColors.size() - 1));
        }
        if (!availableColors.contains(cannon.nextBallColor)) {
            cannon.nextBallColor = availableColors.get(MathUtils.random(availableColors.size() - 1));
        }
    }

    private void draw() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        if (path != null) {
            path.draw(sr);
            Vector2 goalPoint = path.getPositionAt(goalPosition);

            float pulse = 0.8f + 0.2f * MathUtils.sin(dangerTimer);
            sr.setColor(0, 0, 0, 0.5f);
            sr.circle(goalPoint.x, goalPoint.y, 28 * pulse, Constants.CIRCLE_SEGMENTS);
            sr.setColor(Constants.C_CLAY);
            sr.circle(goalPoint.x, goalPoint.y, 22 * pulse, Constants.CIRCLE_SEGMENTS);
        }

        for (Ball ball : balls) {
            if (ball.pathPosition < 0) continue;
            Vector2 pos = path.getPositionAt(ball.pathPosition);
            if (pos != null) {
                ball.position.set(pos);
                sr.setColor(0, 0, 0, 0.2f);
                sr.circle(pos.x + 3, pos.y - 3, Constants.BALL_RADIUS, Constants.CIRCLE_SEGMENTS);
                sr.setColor(Constants.C_GOLD);
                sr.circle(pos.x, pos.y, Constants.BALL_RADIUS + 2, Constants.CIRCLE_SEGMENTS);
                sr.setColor(ball.color);
                sr.circle(pos.x, pos.y, Constants.BALL_RADIUS, Constants.CIRCLE_SEGMENTS);
                sr.setColor(1, 1, 1, 0.35f);
                sr.circle(pos.x - 4, pos.y + 4, Constants.BALL_RADIUS * 0.4f, Constants.CIRCLE_SEGMENTS);
            }
        }

        if (flyingBall != null) {
            sr.setColor(Constants.C_GOLD);
            sr.circle(flyingBall.position.x, flyingBall.position.y, Constants.BALL_RADIUS + 2, Constants.CIRCLE_SEGMENTS);
            sr.setColor(flyingBall.color);
            sr.circle(flyingBall.position.x, flyingBall.position.y, Constants.BALL_RADIUS, Constants.CIRCLE_SEGMENTS);
        }

        for (Particle p : particles) {
            sr.setColor(p.color);
            sr.circle(p.position.x, p.position.y, p.size);
        }

        cannon.draw(sr);
        sr.end();

        game.batch.begin();
        game.font.draw(game.batch, "СЧЕТ: " + (int)score, 20, Constants.HEIGHT - 20);
        game.font.draw(game.batch, "УРОВЕНЬ: " + levelNumber, 20, Constants.HEIGHT - 55);
        if (comboTimer > 0 && combo > 1) {
            game.fontLarge.setColor(Constants.C_GOLD);
            game.fontLarge.draw(game.batch, "КОМБО x" + combo + "!", Constants.WIDTH / 2 - 100, Constants.HEIGHT - 120);
        }
        game.fontSmall.draw(game.batch, "СЛЕДУЮЩИЙ: ", Constants.WIDTH - 230, Constants.HEIGHT - 25);
        game.batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Constants.C_GOLD);
        sr.circle(Constants.WIDTH - 50, Constants.HEIGHT - 35, 14, Constants.CIRCLE_SEGMENTS);
        sr.setColor(cannon.nextBallColor);
        sr.circle(Constants.WIDTH - 50, Constants.HEIGHT - 35, 12, Constants.CIRCLE_SEGMENTS);
        sr.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameOver || paused) return false;

        // Let UI stage check first
        if (uiStage.touchDown(screenX, screenY, pointer, button)) return true;

        isTouching = true;
        touchHoldTime = 0;
        touchSwapped = false;

        if (button == Input.Buttons.RIGHT) {
            cannon.swapColors();
            touchSwapped = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (gameOver || paused) return false;

        if (!touchSwapped && isTouching) {
            Vector2 coords = viewport.unproject(new Vector2(screenX, screenY));
            cannon.aimAt(coords.x, coords.y);
            if (cannon.canShoot && flyingBall == null) {
                flyingBall = cannon.shoot();
                if (flyingBall != null) shotsFired++;
            }
        }

        isTouching = false;
        return true;
    }

    @Override public boolean touchDragged(int x, int y, int p) { return false; }
    @Override public boolean mouseMoved(int x, int y) { return false; }
    @Override public boolean scrolled(float x, float y) { return false; }
    @Override public boolean touchCancelled(int x, int y, int p, int b) {
        isTouching = false;
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.P) {
            togglePause();
            return true;
        }
        if (keycode == Input.Keys.SPACE) {
            cannon.swapColors();
            return true;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char c) { return false; }

    @Override public void show() {
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override public void resize(int w, int h) {
        viewport.update(w, h, true);
        uiStage.getViewport().update(w, h, true);
    }
    @Override public void pause() { paused = true; }
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { sr.dispose(); uiStage.dispose(); }
}
