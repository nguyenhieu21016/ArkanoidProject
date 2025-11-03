
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.powerup.PowerUp;


public class GameManager {

    // Singleton instance
    private static final GameManager INSTANCE = new GameManager(true);

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    
    // Game constants
    private static final int INITIAL_LIVES = 3;
    private static final int SCORE_PER_BRICK = 10;
    private static final int INITIAL_BRICK_ROWS = 6;
    private static final int BRICK_WIDTH = 70;
    private static final int BRICK_HEIGHT = 30;
    private static final int BRICK_OFFSET_Y = 50;
    
    // Paddle constants
    private static final int PADDLE_INIT_X = SCREEN_WIDTH / 2 - 50;
    private static final int PADDLE_INIT_Y = 550;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int PADDLE_SPEED = 12;
    
    // Ball constants
    private static final int BALL_SIZE = 20;
    private static final int BALL_OFFSET_FROM_PADDLE = 2;
    
    // Endless mode constants
    private static final double SPAWN_INTERVAL_SECONDS = 16.0;
    private static final double EMPTY_BRICK_CHANCE = 0.40;
    private static final double NORMAL_BRICK_CHANCE = 0.85;
    private static final int MAX_PATTERN_ATTEMPTS = 5;

    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;
    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    private int score;
    private int lives;
    private GameState currentState;
    private final MenuState menuState = new MenuState();
    
    private String currentPlayerName = "";
    private int scoreToSave = 0;

    private final Random random = new Random();
    private boolean endlessMode = true;
    private double spawnInterval = SPAWN_INTERVAL_SECONDS;
    private double spawnTimer = 0.0;

    private int cols;
    private int offsetX;
    private double lastUpdateNano = System.nanoTime();

    // Power-up effect: expand paddle
    private boolean paddleExpanded = false;
    private double expandTimer = 0.0;
    private int originalPaddleWidth = -1;

    // Transitions
    private boolean isResetting = false;
    private double resetTimer = 0.0;
    private static final double RESET_DURATION = 0.3;

    private final StateTransition stateTransition = new StateTransition();
    private static final double STATE_TRANSITION_DURATION = 0.4;

    // Private constructor cho Singleton; cờ nội bộ để phân biệt với truy cập từ bên ngoài
    private GameManager(boolean internal) {
        this.currentState = GameState.MENU;
        initGame();
    }

    /**
     * Truy cập thể hiện duy nhất của GameManager.
     */
    public static GameManager getInstance() {
        return INSTANCE;
    }

    public void initGame() {
        score = 0;
        lives = INITIAL_LIVES;

        cols = Math.max(1, SCREEN_WIDTH / BRICK_WIDTH);
        offsetX = (SCREEN_WIDTH - cols * BRICK_WIDTH) / 2;

        paddle = new Paddle(PADDLE_INIT_X, PADDLE_INIT_Y, PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_SPEED);
        resetBallAndPaddle();

        // Khởi tạo gạch
        loadLevel();

        // Đặt lại bộ đếm thời gian
        spawnTimer = 0.0;
        lastUpdateNano = System.nanoTime();
        powerUps.clear();
        clearExpandEffect();
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
            startStateTransition(GameState.RUNNING);
            lastUpdateNano = System.nanoTime();
        }
    }

    private void resetBallAndPaddle() {
        if (paddle == null) {
            paddle = new Paddle(PADDLE_INIT_X, PADDLE_INIT_Y, PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_SPEED);
        }
        paddle.setX(SCREEN_WIDTH / 2 - paddle.getWidth() / 2);
        int ballX = paddle.getX() + paddle.getWidth() / 2 - BALL_SIZE / 2;
        int ballY = paddle.getY() - BALL_SIZE - BALL_OFFSET_FROM_PADDLE;
        ball = new Ball(ballX, ballY, BALL_SIZE, 0, 0);
        ball.setLaunched(false);
    }

    private void loadLevel() {
        bricks = new ArrayList<>();

        for (int row = 0; row < INITIAL_BRICK_ROWS; row++) {
            int forcedEmpty = (cols > 0) ? random.nextInt(cols) : -1;
            for (int col = 0; col < cols; col++) {
                if (col == forcedEmpty) continue;

                int x = offsetX + col * BRICK_WIDTH;
                int y = BRICK_OFFSET_Y + row * BRICK_HEIGHT;
                Brick brick = createRandomBrick(x, y);
                if (brick != null) {
                    bricks.add(brick);
                }
            }
        }
    }

    // Sử dụng Factory để tạo gạch ngẫu nhiên, gom logic khởi tạo về một nơi.
    private Brick createRandomBrick(int x, int y) {
        return BrickFactory.createRandomBrick(
                random,
                x,
                y,
                BRICK_WIDTH,
                BRICK_HEIGHT,
                EMPTY_BRICK_CHANCE,
                NORMAL_BRICK_CHANCE
        );
    }

    public void updateGame() {
        long now = System.nanoTime();
        double deltaTime = (now - lastUpdateNano) / 1_000_000_000.0;
        lastUpdateNano = now;

        // Generic state transition update (runs regardless of state)
        stateTransition.update(deltaTime);
        if (stateTransition.shouldSwitchNow()) {
            currentState = stateTransition.getToState();
            stateTransition.markSwitchedHandled();
        }

        if (currentState != GameState.RUNNING) {
            return;
        }

        if (isResetting) {
            resetTimer += deltaTime;
            if (resetTimer >= RESET_DURATION / 2.0) {
                if (ball == null || ball.isLaunched()) {
                    resetBallAndPaddle();
                }
            }
            if (resetTimer >= RESET_DURATION) {
                isResetting = false;
                resetTimer = 0.0;
            }
            // Allow paddle to move during reset and keep ball attached
            paddle.update(SCREEN_WIDTH);
            if (ball != null && !ball.isLaunched()) {
                attachBallToPaddle();
            }
            return;
        }

        // No physics suppression needed; state transitions are handled generically

        paddle.update(SCREEN_WIDTH);
        if (ball != null) {
            if (!ball.isLaunched()) {
                attachBallToPaddle();
            }
            ball.update();
        }

        if (endlessMode) {
            updateEndlessMode(deltaTime);
        }

        checkCollisions();
        updatePowerUps(deltaTime);
        updateEffects(deltaTime);
        updateFloatingTexts();
        cleanupBricks();
        checkBricksReachedPaddle();
        checkWinCondition();
    }

    private void attachBallToPaddle() {
        ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(paddle.getY() - ball.getHeight() - BALL_OFFSET_FROM_PADDLE);
    }

    private void updateEndlessMode(double deltaTime) {
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            addRowAtTop();
        }
    }

    private void updateFloatingTexts() {
        floatingTexts.removeIf(f -> !f.isActive());
        floatingTexts.forEach(FloatingText::update);
    }

    private void cleanupBricks() {
        bricks.removeIf(b -> b.isDestroyed() || 
            b.getY() > SCREEN_HEIGHT + BRICK_HEIGHT || 
            b.getY() < -BRICK_HEIGHT);
    }

    private void checkBricksReachedPaddle() {
        for (Brick b : bricks) {
            if (!b.isDestroyed() && (b.getY() + b.getHeight() >= paddle.getY() - 1)) {
                triggerGameOver();
                return;
            }
        }
    }

    private void triggerGameOver() {
        // Prepare NAME_INPUT data and start transition
        scoreToSave = score;
        currentPlayerName = "";
        startStateTransition(GameState.NAME_INPUT);
    }

    private void handleBrickCollisions() {
        for (Brick brick : new ArrayList<>(bricks)) {
            if (brick.isDestroyed()) continue;
            if (ball.handleCollisionWith(brick)) {
                score += SCORE_PER_BRICK;
                // Sound removed
                double textX = brick.getX() + brick.getWidth() / 2.0;
                floatingTexts.add(new FloatingText(textX, brick.getY(), "+" + SCORE_PER_BRICK));
                if (brick.isDestroyed() && brick instanceof PowerUpBrick) {
                    PowerUp p = ((PowerUpBrick) brick).spawnPowerUp();
                    if (p != null) {
                        powerUps.add(p);
                    }
                }
                break;
            }
        }
    }

    /**
     * Kiểm tra toàn bộ va chạm trong frame hiện tại:
     * - Bóng với tường, đáy màn hình (mất mạng)
     * - Bóng với thanh đỡ (tính toán góc nảy)
     * - Bóng với gạch (cập nhật điểm, hiệu ứng chữ nổi)
     */
    private void checkCollisions() {
        if (ball == null) return;

        // Va chạm với tường: bật lại, đẩy ra khỏi cạnh và đảm bảo có vận tốc ngang tối thiểu
        if (ball.getX() <= 0) {
            ball.bounceX();
            ball.resolveLeftWallCollision();
        } else if (ball.getX() + ball.getWidth() >= SCREEN_WIDTH) {
            ball.bounceX();
            ball.resolveRightWallCollision(SCREEN_WIDTH);
        }
        if (ball.getY() <= 0) {
            ball.bounceY();
        }
        if (ball.getY() + ball.getHeight() >= SCREEN_HEIGHT) {
            handleLifeLost();
            return; // prevent multiple decrements in the same frame
        }

        // Thanh đỡ
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.calculateBounceFromPaddle(paddle);
        }

        handleBrickCollisions();
    }

    /**
     * Thêm một hàng gạch mới ở trên cùng cho chế độ vô tận, đồng thời đẩy các hàng hiện tại xuống dưới.
     * Có logic chống lặp mẫu để tạo cảm giác đa dạng theo thời gian.
     */
    private void addRowAtTop() {
        int topRowY = findTopRowY();
        int[] previousPattern = captureTopRowPattern(topRowY);
        
        shiftBricksDown();
        
        int[] newPattern = generateNewRowPattern(previousPattern);
        spawnNewRow(topRowY, newPattern);
        
        spawnTimer = 0.0;
    }

    private int findTopRowY() {
        int minY = Integer.MAX_VALUE;
        for (Brick b : bricks) {
            if (!b.isDestroyed()) {
                minY = Math.min(minY, b.getY());
            }
        }
        return minY == Integer.MAX_VALUE ? BRICK_OFFSET_Y : minY;
    }

    private int[] captureTopRowPattern(int topRowY) {
        int[] pattern = new int[cols];
        for (Brick b : bricks) {
            if (b.isDestroyed() || b.getY() != topRowY) continue;
            int colIndex = (b.getX() - offsetX) / BRICK_WIDTH;
            if (colIndex >= 0 && colIndex < cols) {
                pattern[colIndex] = (b instanceof StrongBrick) ? 2 : 1;
            }
        }
        return pattern;
    }

    private void shiftBricksDown() {
        for (Brick b : bricks) {
            b.setY(b.getY() + BRICK_HEIGHT);
        }
    }

    private int[] generateNewRowPattern(int[] previousPattern) {
        int[] newPattern = new int[cols];
        for (int attempt = 0; attempt < MAX_PATTERN_ATTEMPTS; attempt++) {
            generateRandomPattern(newPattern);
            if (!isPatternSame(newPattern, previousPattern)) {
                break;
            }
            randomizeOneColumn(newPattern, previousPattern);
        }
        return newPattern;
    }

    private void generateRandomPattern(int[] pattern) {
        for (int c = 0; c < cols; c++) {
            int r = random.nextInt(100);
            if (r < (int)(EMPTY_BRICK_CHANCE * 100)) {
                pattern[c] = 0;
            } else if (r < (int)(NORMAL_BRICK_CHANCE * 100)) {
                pattern[c] = 1;
            } else {
                pattern[c] = 2;
            }
        }
    }

    private boolean isPatternSame(int[] pattern1, int[] pattern2) {
        for (int c = 0; c < cols; c++) {
            if (pattern1[c] != pattern2[c]) {
                return false;
            }
        }
        return true;
    }

    private void randomizeOneColumn(int[] pattern, int[] previousPattern) {
        int colToFlip = random.nextInt(cols);
        pattern[colToFlip] = (previousPattern[colToFlip] == 0) ? 1 : 0;
    }

    private void spawnNewRow(int rowY, int[] pattern) {
        for (int c = 0; c < cols; c++) {
            int x = offsetX + c * BRICK_WIDTH;
            Brick brick = createBrickFromPattern(pattern[c], x, rowY);
            if (brick != null) {
                bricks.add(brick);
            }
        }
    }

    private Brick createBrickFromPattern(int type, int x, int y) {
        return BrickFactory.createFromPattern(type, x, y, BRICK_WIDTH, BRICK_HEIGHT);
    }

    private void handleLifeLost() {
        if (isResetting || stateTransition.isActive()) {
            return;
        }
        lives--;
        if (lives <= 0) {
            triggerGameOver();
        } else {
            // Start smooth reset transition instead of instant reset
            isResetting = true;
            resetTimer = 0.0;
        }
    }

    public boolean isResetting() {
        return isResetting;
    }

    public double getResetProgress() {
        if (!isResetting) return 0.0;
        return Math.min(1.0, resetTimer / RESET_DURATION);
    }

    private void checkWinCondition() {
        boolean allDestroyed = bricks.stream().allMatch(Brick::isDestroyed);
        if (allDestroyed) {
            currentState = GameState.GAME_WON;
        }
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
    public MenuState getMenuState() { return menuState; }
    public List<FloatingText> getFloatingTexts() { return floatingTexts; }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String name) {
        this.currentPlayerName = name;
    }

    public int getScoreToSave() {
        return scoreToSave;
    }

    public void saveHighScore() {
        String nameToSave = currentPlayerName.isEmpty() ? "Player" : currentPlayerName;
        HighScoreManager.getInstance().addScore(nameToSave, scoreToSave);
        startStateTransition(GameState.GAME_OVER);
    }

    public void addLife(int amount) {
        lives += amount;
    }

    private void startStateTransition(GameState targetState) {
        if (currentState == targetState) return;
        stateTransition.start(currentState, targetState, STATE_TRANSITION_DURATION);
    }

    public boolean isTransitionActive() { return stateTransition.isActive(); }
    public double getTransitionProgress() { return stateTransition.getProgress(); }
    public GameState getTransitionFrom() { return stateTransition.getFromState(); }
    public GameState getTransitionTo() { return stateTransition.getToState(); }

    // Deprecated: game over handled by generic stateTransition

    private void updatePowerUps(double deltaTime) {
        for (PowerUp p : new ArrayList<>(powerUps)) {
            p.update();
            if (p.getY() > SCREEN_HEIGHT) {
                powerUps.remove(p);
                continue;
            }
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.apply(this);
                powerUps.remove(p);
            }
        }
    }

    private void updateEffects(double deltaTime) {
        if (paddleExpanded) {
            expandTimer -= deltaTime;
            if (expandTimer <= 0.0) {
                clearExpandEffect();
            }
        }
    }

    private void clearExpandEffect() {
        if (paddleExpanded && originalPaddleWidth > 0) {
            int center = paddle.getX() + paddle.getWidth() / 2;
            paddle.setWidth(originalPaddleWidth);
            paddle.setX(center - paddle.getWidth() / 2);
        }
        paddleExpanded = false;
        expandTimer = 0.0;
        originalPaddleWidth = -1;
    }

    public void applyExpandPaddleEffect(Paddle pad, double scaleFactor, double durationSeconds) {
        if (!paddleExpanded) {
            originalPaddleWidth = pad.getWidth();
            int newWidth = (int) Math.round(originalPaddleWidth * scaleFactor);
            int center = pad.getX() + pad.getWidth() / 2;
            pad.setWidth(newWidth);
            pad.setX(center - pad.getWidth() / 2);
            paddleExpanded = true;
        }
        expandTimer = durationSeconds;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
