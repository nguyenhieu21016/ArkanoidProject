package view;

import javafx.scene.text.Text;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import util.AssetManager;
import model.manager.GameManager;
import model.entity.Ball;
import model.entity.Paddle;
import model.brick.Brick;
import model.brick.PowerUpBrick;
import model.brick.StrongBrick;
import model.ui.FloatingText;
import model.powerup.PowerUp;
import model.powerup.PowerUpType;

import java.util.List;

public class GameView {

    private final GameManager gameManager;
    private final Pane root;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameMenu gameMenu = new GameMenu();

    public GameView(GameManager gameManager) {
        this.gameManager = gameManager;
        this.canvas = new Canvas(GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.root = new Pane(canvas);
    }

    public Pane getRoot() {
        return root;
    }

    private static final double OVERLAY_OPACITY = 0.5;
    private static final int CURSOR_BLINK_INTERVAL_MS = 500;

    public void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameManager.updateGame();
                render();
            }
        }.start();
    }

    private void render() {
        renderBackground();

        switch (gameManager.getCurrentState()) {
            case MENU:
                gameMenu.render(gc, gameManager.getMenuState());
                break;
            case RUNNING:
                renderGamePlay();
                if (gameManager.isResetting()) {
                    renderResetTransition();
                }
                break;
            case PAUSED:
                renderGamePlay();
                gameMenu.renderPause(gc, gameManager.getPauseMenuState());
                break;
            case NAME_INPUT:
                renderGamePlay();
                renderNameInput();
                if (gameManager.isResetting()) {
                    renderResetTransition();
                }
                break;
            case GAME_OVER:
                renderGamePlay();
                renderEndGameMessage("GAME OVER", Color.web("#B8F4DC"));
                break;
            case GAME_WON:
                renderGamePlay();
                renderEndGameMessage("GAME WON!", Color.web("#B8F4DC"));
                break;
            case HIGHSCORE:
                gameMenu.renderHighScores(gc);
                break;
            case INSTRUCTION:
                gameMenu.renderInstruction(gc);
                break;
            case SETTINGS:
                gameMenu.renderSettings(gc, gameManager.getSettingsState());
                break;
        }

        if (gameManager.isTransitionActive()) {
            renderStateTransitionOverlay();
        }
    }

    private void renderBackground() {
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }
    }

    private void renderOverlay(double opacity) {
        gc.setFill(Color.color(0, 0, 0, opacity));
        gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
    }

    private void renderGamePlay() {
        Paddle paddle = gameManager.getPaddle();
        Ball ball = gameManager.getBall();
        List<Brick> bricks = gameManager.getBricks();
        List<PowerUp> powerUps = gameManager.getPowerUps();

        renderPaddle(paddle);
        renderBall(ball);
        for (Ball eb : gameManager.getExtraBalls()) {
            renderBall(eb);
        }

        // Vẽ Bricks
        renderBricks(bricks);
        renderPowerUps(powerUps);
        renderFloatingTexts();
        renderHUD();
    }

    private void renderPaddle(Paddle paddle) {
        Image sprite = AssetManager.getInstance().getImage("paddle");
        if (sprite != null) {
            gc.drawImage(sprite, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        } else {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }
    }

    private void renderBall(Ball ball) {
        Image sprite = AssetManager.getInstance().getImage("ball");
        if (sprite != null) {
            gc.drawImage(sprite, ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
        }
    }

    private void renderBricks(List<Brick> bricks) {
        Image normalSprite = AssetManager.getInstance().getImage("normal_brick");
        Image strongSprite = AssetManager.getInstance().getImage("strong_brick");
        Image crackedSprite = AssetManager.getInstance().getImage("strong_brick_cracked");
        Image powerupSprite = AssetManager.getInstance().getImage("powerup_brick");

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                Image sprite = getBrickSprite(brick, normalSprite, strongSprite, crackedSprite, powerupSprite);
                Color fallbackColor = (brick instanceof StrongBrick) ? Color.DARKGRAY : Color.ORANGE;

                if (sprite != null) {
                    gc.drawImage(sprite, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                } else {
                    gc.setFill(fallbackColor);
                    gc.fillRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                }

                gc.setStroke(Color.BLACK);
                gc.strokeRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
            }
        }
    }

    private Image getBrickSprite(Brick brick, Image normalSprite, Image strongSprite, Image crackedSprite, Image powerupSprite) {
        if (brick instanceof PowerUpBrick) {
            return powerupSprite != null ? powerupSprite : normalSprite;
        }
        if (brick instanceof StrongBrick) {
            try {
                if (brick.getHitPoints() == 1 && crackedSprite != null) {
                    return crackedSprite;
                }
            } catch (NoSuchMethodError | AbstractMethodError e) {
                // Fallback to strong sprite
            }
            return strongSprite;
        }
        return normalSprite;
    }

    private void renderFloatingTexts() {
        for (FloatingText ft : gameManager.getFloatingTexts()) {
            ft.render(gc);
        }
    }

    private void renderPowerUps(List<PowerUp> powerUps) {
        Image expandSprite = AssetManager.getInstance().getImage("powerup_expand");
        Image multiSprite = AssetManager.getInstance().getImage("powerup_multi");
        Image extraLifeSprite = AssetManager.getInstance().getImage("powerup_extralife");

        gc.setStroke(Color.BLACK);
        for (PowerUp p : powerUps) {
            Image sprite = null;
            if (p.getType() == PowerUpType.EXPAND) sprite = expandSprite;
            else if (p.getType() == PowerUpType.MULTI) sprite = multiSprite;
            else if (p.getType() == PowerUpType.EXTRA_LIFE) sprite = extraLifeSprite;

            if (sprite != null) {
                gc.drawImage(sprite, p.getX(), p.getY(), p.getWidth(), p.getHeight());
            } else {
                // Fallback: simple colored badge
                Color color = (p.getType() == PowerUpType.EXPAND) ? Color.LIMEGREEN : (p.getType() == PowerUpType.MULTI ? Color.CYAN : Color.GOLD);
                String label = (p.getType() == PowerUpType.EXPAND) ? "E" : (p.getType() == PowerUpType.MULTI ? "M" : "+1");
                gc.setFill(color);
                gc.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());
                gc.strokeOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("m6x11", 12));
                double tx = p.getX() + p.getWidth() / 2.0 - (label.length() == 1 ? 4 : 8);
                double ty = p.getY() + p.getHeight() / 2.0 + 4;
                gc.fillText(label, tx, ty);
            }
        }
    }

    private void renderHUD() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 20));
        gc.fillText("Score: " + gameManager.getScore(), 10, 25);
        gc.fillText("Lives: " + gameManager.getLives(), GameManager.SCREEN_WIDTH - 80, 25);
        
        // Hiển thị combo
        int combo = gameManager.getComboCount();
        if (combo >= 2) {
            gc.setFont(new Font("m6x11", 24));
            gc.setFill(Color.web("#FFD700")); // Gold color for combo
            String comboText = combo + "x COMBO!";
            javafx.scene.text.Text textNode = new javafx.scene.text.Text(comboText);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double comboX = (GameManager.SCREEN_WIDTH - textWidth) / 2.0;
            gc.fillText(comboText, comboX, 50);
        }

        // Spawn countdown text (endless mode)
        if (gameManager.isEndlessMode()) {
            double remaining = gameManager.getSpawnTimeRemainingSeconds();
            int secs = Math.max(0, (int) Math.ceil(remaining));
            String text = String.format("NEXT ROW: %ds", secs);
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("m6x11", 26));
            Text t = new Text(text);
            t.setFont(gc.getFont());
            double textWidth = t.getLayoutBounds().getWidth();
            double tx = (GameManager.SCREEN_WIDTH - textWidth) / 2.0;
            double ty = 30;
            gc.fillText(text, tx, ty);
        }
    }



    private void renderEndGameMessage(String message, Color color) {
        renderOverlay(OVERLAY_OPACITY);

        gc.setFill(color);
        gc.setFont(new Font("m6x11", 50));
        drawTextCentered(message, 0);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 20));
        drawTextCentered("Press SPACE to play again", 40);
    }

    private void drawTextCentered(String text, double yOffset) {
        Text textNode = new Text(text);
        textNode.setFont(gc.getFont());
        double textWidth = textNode.getLayoutBounds().getWidth();
        gc.fillText(text, (GameManager.SCREEN_WIDTH - textWidth) / 2, GameManager.SCREEN_HEIGHT / 2.0 + yOffset);
    }

    private void renderNameInput() {
        renderOverlay(OVERLAY_OPACITY);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 40));
        drawTextCentered("Enter Your Name", -100);

        String playerName = gameManager.getCurrentPlayerName();
        String displayName = formatNameWithCursor(playerName);

        gc.setFill(Color.web("#B8F4DC"));
        gc.setFont(new Font("m6x11", 32));
        drawTextCentered(displayName, -30);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 24));
        drawTextCentered("Score: " + gameManager.getScoreToSave(), 20);

        gc.setFont(new Font("m6x11", 18));
        drawTextCentered("Type your name and press ENTER", 70);
        drawTextCentered("Press BACKSPACE to delete", 100);
    }

    private String formatNameWithCursor(String playerName) {
        String displayName = playerName.isEmpty() ? "_" : playerName + "_";
        long currentTime = System.currentTimeMillis();
        boolean showCursor = (currentTime / CURSOR_BLINK_INTERVAL_MS) % 2 == 0;
        if (!showCursor && !playerName.isEmpty()) {
            displayName = playerName;
        }
        return displayName;
    }

    private void renderResetTransition() {
        double progress = gameManager.getResetProgress();
        double opacity;

        if (progress < 0.5) {
            opacity = progress * 2.0;
        } else {
            opacity = (1.0 - progress) * 2.0;
        }

        opacity = Math.min(0.7, opacity);
        renderOverlay(opacity);

        gc.setFill(Color.web("#B8F4DC"));
        gc.setFont(new Font("m6x11", 48));
        drawTextCentered("Life Lost!", 0);
    }

    private void renderStateTransitionOverlay() {
        double p = gameManager.getTransitionProgress();
        double opacity = p < 0.5 ? (p * 2.0) : ((1.0 - p) * 2.0);
        opacity = Math.min(0.7, Math.max(0.0, opacity));
        renderOverlay(opacity);
    }
}
