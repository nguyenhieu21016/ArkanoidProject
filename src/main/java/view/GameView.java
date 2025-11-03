package view;

import javafx.scene.text.Text;
import model.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import util.AssetManager;

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
    private static final double PAUSED_OVERLAY_OPACITY = 0.6;
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
                break;
            case PAUSED:
                renderGamePlay();
                renderOverlay(PAUSED_OVERLAY_OPACITY);

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("m6x11", 60));
                drawTextCentered("PAUSED", -20);
                gc.setFont(new Font("m6x11", 20));
                drawTextCentered("Press P to Resume", 20);
                drawTextCentered("Press R to Restart", 50);
                break;
            case NAME_INPUT:
                renderGamePlay();
                renderNameInput();
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

        renderPaddle(paddle);
        renderBall(ball);

        // Vẽ Bricks
        renderBricks(bricks);
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

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                Image sprite = getBrickSprite(brick, normalSprite, strongSprite, crackedSprite);
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

    private Image getBrickSprite(Brick brick, Image normalSprite, Image strongSprite, Image crackedSprite) {
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

    private void renderHUD() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 20));
        gc.fillText("Score: " + gameManager.getScore(), 10, 25);
        gc.fillText("Lives: " + gameManager.getLives(), GameManager.SCREEN_WIDTH - 80, 25);
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
}
