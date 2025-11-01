package model;

import view.GameMenu;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;
    private final List<FloatingText> floatingTexts = new ArrayList<>();

    private int score;
    private int lives;
    private GameState currentState;
    private final GameMenu menu = new GameMenu();

    private boolean endlessMode = true;
    private double spawnInterval = 4.0;
    private double spawnTimer = 0.0;


    /**
     * Constructor để khởi tạo GameManager.
     */
    public GameManager() {
        this.currentState = GameState.MENU;
        initGame();
    }

    /**
     * Thiết lập các trạng thái ban đầu.
     */
    public void initGame() {
        score = 0;
        lives = 3;

        // Khởi tạo Paddle ở dưới giữa màn hình
        paddle = new Paddle(SCREEN_WIDTH / 2 - 50, 550, 100, 20, 10);

        // Khởi tạo Ball ngay trên màn hình
        resetBallandPaddle();

        // Tạo các Brick cho màn chơi
        loadLevel();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public void startGame() {
        if (currentState != GameState.RUNNING) {
            initGame();
            currentState = GameState.RUNNING;
        }
    }


    /**
     * Reset vị trí của Ball và Paddle.
     */
    private void resetBallandPaddle() {
        paddle.setX(SCREEN_WIDTH / 2 - paddle.getWidth() / 2);
        ball = new Ball(paddle.getX() + paddle.getWidth() / 2 - 10, paddle.getY() - 20, 20, 0, 0);
        ball.setLaunched(false);
    }

    /**
     * Tạo màn chơi.
     */
    private void loadLevel() {
        bricks = new ArrayList<>();
        int brickWidth = 70;
        int brickHeight = 30;
        int offsetX = 50;
        int offsetY = 50;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 10; col++) {
                int x = offsetX + col * brickWidth;
                int y = offsetY + row * brickHeight;

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
        if (currentState != GameState.RUNNING) {
            return;
        }
        // Cập nhật vị trí các đối tượng di chuyển
        paddle.update(SCREEN_WIDTH);
        if (ball != null && !ball.isLaunched()) {
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - 10);
            ball.setY(paddle.getY() - 15);
        }
        ball.update();

        // Kiểm tra va chạm
        checkCollisions();
        floatingTexts.removeIf(f-> !f.isActive());
        floatingTexts.forEach(FloatingText::update);

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
            ball.calculateBounceFromPaddle(paddle);
        }

        // Va chạm giữa Ball và Brick
        for (Brick brick : bricks) {
            if (brick.isDestroyed()) {
                continue;
            }
            if (ball.handleCollisionWith(brick)) {
                score += 10;
                floatingTexts.add(new FloatingText(brick.getX() + brick.getWidth() / 2.0, brick.getY(), "+10"));
                // Chỉ xử lí va chạm với 1 gạch mỗi frame
                return;
            }
        }
    }

    /**
     * Xử lí mất mạng.
     */
    private void handleLifeLost() {
        lives--;
        if (lives <= 0) {
            currentState = GameState.GAME_OVER;
            HighScoreManager.getInstance().addScore("Player", score);
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
        currentState = GameState.GAME_WON;
    }

    /**
     * Dừng game.
     */
    public void pauseGame() {
        if (currentState == GameState.RUNNING) {
            currentState = GameState.PAUSED;
        }
    }
    public void resumeGame() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.RUNNING;
        }
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

    public GameMenu getMenu() {
        return menu;
    }

    public List<FloatingText> getFloatingTexts() {
        return floatingTexts;
    }
}
