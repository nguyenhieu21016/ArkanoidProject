package model;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;

    private int score;
    private int lives;
    private boolean isGameOver;
    private boolean isGameWon;

    /**
     * Constructor để khởi tạo GameManager.
     */
    public GameManager() {
        initGame();
    }

    /**
     * Thiết lập các trạng thái ban đầu.
     */
    public void initGame() {
        score = 0;
        lives = 3;
        isGameOver = false;
        isGameWon = true;

        // Khởi tạo Paddle ở dưới giữa màn hình
        paddle = new Paddle(SCREEN_HEIGHT / 2 - 50, 550, 100, 20, 15);

        // Khởi tạo Ball ngay trên màn hình
        resetBallandPaddle();

        // Tạo các Brick cho màn chơi
        loadLevel();
    }

    /**
     * Reset vị trí của Ball và Paddle.
     */
    private void resetBallandPaddle() {
        paddle.setX(SCREEN_WIDTH / 2 - paddle.getWidth() / 2);
        ball = new Ball(paddle.getX() + paddle.getWidth() / 2 - 10, paddle.getY() - 20, 20, 4, -4);
    }

    /**
     * Tạo màn chơi.
     */
    private void loadLevel() {
        bricks = new ArrayList<>();
        int brickWidth = 75;
        int brickHeight = 30;
        int padding = 10;
        int offsetX = 50;
        int offsetY = 50;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 8; col++) {
                int x = offsetX + col * (brickWidth + padding);
                int y = offsetY + row * (brickHeight + padding);

                // Tạo xen kẽ gạch thường và gạch cứng
                if (row % 2 == 0) {
                    bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                } else {
                    bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                }
            }
        }
    }

    /**
     * Update chính của game.
     */
    public void updateGame() {
        if (isGameOver || isGameWon) {
            return;
        }

        // Cập nhật vị trí các đối tượng di chuyển
        paddle.update(SCREEN_WIDTH);
        ball.update();

        // Kiểm tra va chạm
        checkCollisions();

        // Kiểm tra điều kiện thắng
        checkWinCondition();
    }

    /**
     * Xử lí va chạm.
     */
    private void checkCollisions() {
        // Va chạm giữa Ball và tưởng
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= SCREEN_WIDTH) {
            ball.bounceX();
        }
        if (ball.getY() <= 0) {
            ball.bounceY();
        }
        if (ball.getY() + ball.getHeight() >= SCREEN_HEIGHT) {
            handleLifeLost();
        }

        // Va chạm giữa Ball và Paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.bounceOffPaddle(paddle);
        }

        // Va chạm giữa Ball và Brick
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getBounds())) {
                ball.bounceY();
                brick.takeHit();

                if (brick.isDestroyed()) {
                    score += 10;
                }

                // Chỉ xử lí va chạm với 1 gạch mỗi frame
                break;
            }
        }
    }

    /**
     * Xử lí mất mạng.
     */
    private void handleLifeLost() {
        lives--;
        if (lives <= 0) {
            isGameOver = true;
        } else {
            resetBallandPaddle();
        }
    }

    /**
     * Kiểm tra thắng game.
     */
    private void checkWinCondition() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return;
            }
        }
        isGameWon = true;
    }

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isGameWon() {
        return isGameWon;
    }
}
