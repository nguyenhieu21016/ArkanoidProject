package model;

import view.GameMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    // --- cấu hình endless / bố cục ---
    private final Random random = new Random();
    private boolean endlessMode = true;
    private double spawnInterval = 12.0;   // số giây giữa các hàng (sinh chậm hơn nữa)
    private double spawnTimer = 0.0;

    private int brickWidth = 70;
    private int brickHeight = 30;
    private int cols;    // tính toán một lần trong initGame()
    private int offsetX; // tính toán một lần trong initGame()

    // tinh chỉnh sinh hàng/thua mạng
    private double lastUpdateNano = System.nanoTime();

    /**
     * Hàm khởi tạo
     */
    public GameManager() {
        this.currentState = GameState.MENU;
        initGame();
    }

    /**
     * Khởi tạo/đặt lại trạng thái game
     */
    public void initGame() {
        score = 0;
        lives = 3;

        // tính toán bố cục lưới (không có padding) và căn giữa theo chiều ngang
        cols = Math.max(1, SCREEN_WIDTH / brickWidth);
        offsetX = (SCREEN_WIDTH - cols * brickWidth) / 2;

        // khởi tạo thanh đỡ và bóng
        paddle = new Paddle(SCREEN_WIDTH / 2 - 50, 550, 100, 20, 10);
        resetBallandPaddle();

        // khởi tạo gạch
        loadLevel();

        // đặt lại bộ đếm thời gian
        spawnTimer = 0.0;
        lastUpdateNano = System.nanoTime();
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
            lastUpdateNano = System.nanoTime();
        }
    }

    /**
     * Đặt lại vị trí bóng và thanh đỡ
     */
    private void resetBallandPaddle() {
        if (paddle == null) {
            paddle = new Paddle(SCREEN_WIDTH / 2 - 50, 550, 100, 20, 10);
        }
        paddle.setX(SCREEN_WIDTH / 2 - paddle.getWidth() / 2);
        ball = new Ball(paddle.getX() + paddle.getWidth() / 2 - 10, paddle.getY() - 20, 20, 0, 0);
        ball.setLaunched(false);
    }

    /**
     * Tạo màn chơi ban đầu (sử dụng cols & offsetX để khớp với hàng endless)
     */
    private void loadLevel() {
        // tạo hàng gạch ban đầu ngẫu nhiên để giống chế độ endless
        bricks = new ArrayList<>();
        int offsetY = 50;
        int rows = 6;

        for (int row = 0; row < rows; row++) {
            // đảm bảo mỗi hàng có ít nhất một cột trống để tránh khởi đầu không thể chơi
            int forcedEmpty = (cols > 0) ? random.nextInt(cols) : -1;
            for (int col = 0; col < cols; col++) {
                int x = offsetX + col * brickWidth;
                int y = offsetY + row * brickHeight;

                if (col == forcedEmpty) continue; // giữ một ô trống được đảm bảo

                int r = random.nextInt(100);
                if (r < 40) {
                    // ô trống (40%)
                    continue;
                } else if (r < 85) {
                    // gạch thường
                    bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                } else {
                    // gạch bền
                    bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                }
            }
        }
    }

    /**
     * Cập nhật chính — được gọi mỗi khung hình bởi GameView
     */
    public void updateGame() {
        if (currentState != GameState.RUNNING) {
            // nếu đang tạm dừng hoặc trong menu, không cập nhật logic game
            lastUpdateNano = System.nanoTime();
            return;
        }

        long now = System.nanoTime();
        double dt = (now - lastUpdateNano) / 1_000_000_000.0; // giây
        lastUpdateNano = now;

        paddle.update(SCREEN_WIDTH);
        if (ball != null) {
            if (!ball.isLaunched()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight() - 2);
            }
            ball.update();
        }

        if (endlessMode) {
            spawnTimer += dt;
            if (spawnTimer >= spawnInterval) {
                spawnTimer -= spawnInterval;
                addRowAtTop();
            }
        }

        checkCollisions();
        floatingTexts.removeIf(f -> !f.isActive());
        floatingTexts.forEach(FloatingText::update);

        bricks.removeIf(b -> b.isDestroyed() || b.getY() > SCREEN_HEIGHT + brickHeight || b.getY() < -brickHeight);

        for (Brick b : bricks) {
            if (!b.isDestroyed() && (b.getY() + b.getHeight() >= paddle.getY() - 1)) {
                currentState = GameState.GAME_OVER;
                HighScoreManager.getInstance().addScore("Player", score);
                return;
            }
        }

        // kiểm tra thắng
        checkWinCondition();
    }

    /**
     * Xử lý va chạm
     */
    private void checkCollisions() {
        if (ball == null) return;

        // va chạm với tường
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= SCREEN_WIDTH) {
            ball.bounceX();
        }
        if (ball.getY() <= 0) {
            ball.bounceY();
        }
        if (ball.getY() + ball.getHeight() >= SCREEN_HEIGHT) {
            handleLifeLost();
        }

        // thanh đỡ
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.calculateBounceFromPaddle(paddle);
        }

        // bóng va chạm với gạch
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            if (ball.handleCollisionWith(brick)) {
                score += 10;
                floatingTexts.add(new FloatingText(brick.getX() + brick.getWidth() / 2.0, brick.getY(), "+10"));
                break;
            }
        }
    }

    /**
     * Dịch gạch xuống và sinh hàng mới ở trên cùng (xuất hiện từ phía trên)
     */
    private void addRowAtTop() {
        // tìm vị trí Y cao nhất hiện tại
        int prevMinY = Integer.MAX_VALUE;
        for (Brick b : bricks) {
            if (!b.isDestroyed()) prevMinY = Math.min(prevMinY, b.getY());
        }
        if (prevMinY == Integer.MAX_VALUE) prevMinY = 50;

        int[] prevPattern = new int[cols];
        for (int c = 0; c < cols; c++) prevPattern[c] = 0;
        for (Brick b : bricks) {
            if (b.isDestroyed()) continue;
            if (b.getY() == prevMinY) {
                int colIndex = (b.getX() - offsetX) / brickWidth;
                if (colIndex >= 0 && colIndex < cols) {
                    prevPattern[colIndex] = (b instanceof StrongBrick) ? 2 : 1;
                }
            }
        }

        for (Brick b : bricks) b.setY(b.getY() + brickHeight);

        int[] newPattern = new int[cols];
        for (int attempt = 0; attempt < 5; attempt++) {
            for (int c = 0; c < cols; c++) {
                int r = random.nextInt(100);
                if (r < 40) newPattern[c] = 0; // empty
                else if (r < 85) newPattern[c] = 1; // normal
                else newPattern[c] = 2; // strong
            }
            // nếu giống nhau, đảo một cột ngẫu nhiên để tránh lặp lại
            boolean same = true;
            for (int c = 0; c < cols; c++) if (newPattern[c] != prevPattern[c]) { same = false; break; }
            if (!same) break;
            int flip = random.nextInt(cols);
            newPattern[flip] = (prevPattern[flip] == 0) ? 1 : 0;
        }

        int newRowY = prevMinY;
        int placed = 0;
        for (int c = 0; c < cols; c++) {
            int x = offsetX + c * brickWidth;
            if (newPattern[c] == 1) {
                bricks.add(new NormalBrick(x, newRowY, brickWidth, brickHeight));
                placed++;
            } else if (newPattern[c] == 2) {
                bricks.add(new StrongBrick(x, newRowY, brickWidth, brickHeight));
                placed++;
            }
        }

        spawnTimer = 0.0;
    }

    private void handleLifeLost() {
        lives--;
        if (lives <= 0) {
            currentState = GameState.GAME_OVER;
            HighScoreManager.getInstance().addScore("Player", score);
        } else {
            resetBallandPaddle();
        }
    }

    private void checkWinCondition() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) return;
        }
        currentState = GameState.GAME_WON;
    }

    public void pauseGame() {
        if (currentState == GameState.RUNNING) {
            currentState = GameState.PAUSED;
            spawnTimer = 0.0;
        }
    }

    public void resumeGame() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.RUNNING;
            lastUpdateNano = System.nanoTime();
        }
    }

    public Ball getBall() { return ball; }
    public Paddle getPaddle() { return paddle; }
    public List<Brick> getBricks() { return bricks; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public GameMenu getMenu() { return menu; }
    public List<FloatingText> getFloatingTexts() { return floatingTexts; }
}
