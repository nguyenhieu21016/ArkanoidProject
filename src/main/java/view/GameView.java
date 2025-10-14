package view;

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

                // Dừng game nếu thắng hoặc thua
                if (gameManager.isGameOver() || gameManager.isGameWon()) {
                    renderEndGameMessage();
                    this.stop();
                }
            }
        }.start();
    }

    private void render() {
        // Vẽ nền
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }


        // Lấy các đối tượng
        Paddle paddle = gameManager.getPaddle();
        Ball ball = gameManager.getBall();
        List<Brick> bricks = gameManager.getBricks();

        // Vẽ Paddle
        Image paddleSprite = AssetManager.getInstance().getImage("paddle");
        if ((paddleSprite != null)) {
            gc.drawImage(paddleSprite, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getWidth());
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

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                Image brickSpriteToDraw = null;
                Color brickColortoDraw = Color.ORANGE;

                if (brick instanceof StrongBrick) {
                    brickSpriteToDraw = strongBrickSprite;
                    brickColortoDraw = Color.DARKGRAY;
                } else {
                    brickSpriteToDraw = normalBrickSprite;
                }

                if ((brickSpriteToDraw != null)) {
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

        // Vẽ Score & Lives
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Score: " + gameManager.getScore(), 10, 25);
        gc.fillText("Lives: " + gameManager.getLives(), GameManager.SCREEN_WIDTH - 80, 25);
    }

    private void renderEndGameMessage() {
        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 50));
        if (gameManager.isGameOver()) {
            gc.fillText("GAME OVER", GameManager.SCREEN_WIDTH / 2.0 - 150, GameManager.SCREEN_HEIGHT / 2.0);
        } else if (gameManager.isGameWon()) {
            gc.setFill(Color.GREEN);
            gc.fillText("YOU WON!", GameManager.SCREEN_WIDTH / 2.0 - 120, GameManager.SCREEN_HEIGHT / 2.0);
        }
    }
}
