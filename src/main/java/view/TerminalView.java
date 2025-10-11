package view;

import model.Ball;
import model.Brick;
import model.GameManager;
import model.Paddle;

import java.awt.*;

public class TerminalView {
    private final int terminalWidth;
    private final int terminalHeight;

    // Canvas
    private char[][] screenBuffer;

    /**
     * Constructor để tạo Terminal.
     * @param width chiều rộng
     * @param height chiều cao
     */
    public TerminalView(int width, int height) {
        this.terminalHeight = height;
        this.terminalWidth = width;
        this.screenBuffer = new char[height][width];
    }

    /**
     * Render chính.
     * @param gameManager gameManager
     */
    public void render(GameManager gameManager) {
        // Xóa canvas
        clearBuffer();

        // Vẽ Object
        drawBricks(gameManager.getBricks());
        drawPaddle(gameManager.getPaddle());
        drawBall(gameManager.getBall());

        // In canvas
        printBuffer();

        // In score và lives
        System.out.println("Score: " + gameManager.getScore() + "| Lives: " + gameManager.getLives());

        if(gameManager.isGameOver()) {
            System.out.println("GAME OVER!");
        }
        if (gameManager.isGameWon()) {
            System.out.println("YOU WON!");
        }
    }

    /**
     * Xóa canvas.
     */
    private void clearBuffer() {
        for (int i = 0; i < terminalHeight; i++) {
            for (int j = 0; j < terminalWidth; j++) {
                screenBuffer[i][j] = '.';
            }
        }
    }

    /**
     * Vẽ Paddle.
     * @param paddle paddle
     */
    private void drawPaddle(Paddle paddle) {
        int termX = scaleX(paddle.getX());
        int termY = scaleY(paddle.getY());
        int termWidth = scaleX(paddle.getWidth());

        for (int i = 0; i < termWidth; i++) {
            if (termX + i < terminalWidth) {
                screenBuffer[termY][termX + i] = '=';
            }
        }
    }

    /**
     * Vẽ Ball.
     * @param ball ball
     */
    private void drawBall(Ball ball) {
        int termX = scaleX(ball.getX());
        int termY = scaleY(ball.getY());
        screenBuffer[termY][termX] = 'O';
    }

    /**
     * Vẽ Bricks.
     * @param bricks bricks.
     */
    private void drawBricks(Iterable<Brick> bricks) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                int termX = scaleX(brick.getX());
                int termY = scaleY(brick.getY());
                int termWidth = scaleX(brick.getWidth());

                for (int i = 0; i < termWidth; i++) {
                    if (termX + i < terminalWidth) {
                        screenBuffer[termY][termX + i] = '#';
                    }
                }
            }
        }
    }

    /**
     * In canvas.
     */
    private void printBuffer() {
        // Xóa màn hình cũ (dũng mã ANSI escape code)
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int  i = 0; i < terminalHeight; i++) {
            System.out.println(new String(screenBuffer[i]));
        }
    }

    /**
     * Chuyển tọa độ game sang tọa độ terminal.
     * @param gameX gameX
     * @return
     */
    private int scaleX(int gameX) {
        return (int) ((double) gameX / GameManager.SCREEN_WIDTH * terminalWidth);
    }

    /**
     * Chuyển tọa độ game sang tọa độ terminal.
     * @param gameY gameY
     * @return
     */
    private int scaleY(int gameY) {
        return (int) ((double) gameY / GameManager.SCREEN_HEIGHT * terminalHeight);
    }
}
