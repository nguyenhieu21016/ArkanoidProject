package view;

import javafx.scene.input.KeyCode;
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

import java.awt.*;
import java.util.List;

public class GameView {

    private final GameManager gameManager;
    private final Pane root; // Layout container
    private final Canvas canvas; // Canvas
    private final GraphicsContext gc; // Bút

    public GameView(GameManager gameManager) {
        this.gameManager = gameManager;
        this.canvas = new Canvas(GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.root = new Pane(canvas);
    }

    public Pane getRoot() {
        return root;
    }

    public void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Cập nhật logic game
                gameManager.updateGame();

                // Render
                render();
            }
        }.start();
    }

    /**
     * Render tổng, quyết định xem nên vẽ màn hình nào.
     */
    private void render() {
        // Vẽ nền
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        switch (gameManager.getCurrentState()) {
            case MENU:
                gameManager.getMenu().render(gc);
                break;
            case RUNNING:
                renderGamePlay();
                break;
            case PAUSED:
                renderGamePlay(); // vẫn vẽ gameplay hiện tại
                //if                Image pauseBg = AssetManager.getInstance().getImage("pause_background");
                //(pauseBg != null) {
                    //gc.drawImage(pauseBg, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
                //} else {
                    gc.setFill(Color.color(0, 0, 0, 0.6)); // overlay mờ
                    gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
                //}

                gc.setFill(Color.WHITE);
                gc.setFont(new Font("m6x11", 60));
                drawTextCentered("PAUSED", -20);
                gc.setFont(new Font("m6x11", 20));
                drawTextCentered("Press P to Resume", 20);
                drawTextCentered("Press R to Restart", 50);
                break;
            case GAME_OVER:
                renderGamePlay();
                renderEndGameMessage("GAME OVER", Color.web("#B8F4DC"));
                break;
            case GAME_WON:
                renderGamePlay();
                renderEndGameMessage("GAME WON!", Color.web("#B8F4DC"));
                break;
        }
    }

    /**
     * Vẽ các đối tượng game khi đang chơi.
     */
    private void renderGamePlay() {
        // Lấy các đối tượng
        Paddle paddle = gameManager.getPaddle();
        Ball ball = gameManager.getBall();
        List<Brick> bricks = gameManager.getBricks();

        // Vẽ Paddle
        Image paddleSprite = AssetManager.getInstance().getImage("paddle");
        if ((paddleSprite != null)) {
            gc.drawImage(paddleSprite, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        } else {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }

        // Vẽ Ball
        Image ballSprite = AssetManager.getInstance().getImage("ball");
        if (ballSprite != null) {
            gc.drawImage(ballSprite, ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
        }

        // Vẽ Bricks
        Image normalBrickSprite = AssetManager.getInstance().getImage("normal_brick");
        Image strongBrickSprite = AssetManager.getInstance().getImage("strong_brick");
        Image strongCrackedSprite = AssetManager.getInstance().getImage("strong_brick_cracked");

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                Image brickSpriteToDraw = null;
                Color brickColortoDraw = Color.ORANGE;

                if (brick instanceof StrongBrick) {
                    try {
                        if (brick.getHitPoints() == 1 && strongCrackedSprite != null) {
                            brickSpriteToDraw = strongCrackedSprite;
                        } else {
                            brickSpriteToDraw = strongBrickSprite;
                        }
                    } catch (NoSuchMethodError | AbstractMethodError e) {
                        // fallback in case getHitPoints() not present
                        brickSpriteToDraw = strongBrickSprite;
                    }
                    brickColortoDraw = Color.DARKGRAY;
                } else {
                    brickSpriteToDraw = normalBrickSprite;
                }

                if (brickSpriteToDraw != null) {
                    gc.drawImage(brickSpriteToDraw, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                } else {
                    gc.setFill(brickColortoDraw);
                    gc.fillRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
                }

                // Vẽ viền
                gc.setStroke(Color.BLACK);
                gc.strokeRect(brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight());
            }
        }

        for (FloatingText ft : gameManager.getFloatingTexts()) {
            ft.render(gc);
        }

        // Vẽ Score & Lives
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 20));
        gc.fillText("Score: " + gameManager.getScore(), 10, 25);
        gc.fillText("Lives: " + gameManager.getLives(), GameManager.SCREEN_WIDTH - 80, 25);
    }

    /**
     * Vẽ màn hình menu bắt đầu.
     */
    private void renderMenuScreen() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 40));
        drawTextCentered("ARKANOID", -50);

        gc.setFont(new Font("m6x11", 25));
        drawTextCentered("Press SPACE to Play AGAIN", 0);
    }


    /**
     * Vẽ thông báo kết thúc game.
     * @param message thông báo
     * @param color màu chữ
     */
    private void renderEndGameMessage(String message, Color color) {
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
}
