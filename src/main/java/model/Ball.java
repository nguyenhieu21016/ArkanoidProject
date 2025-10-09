package model;

import java.awt.Color;
import java.awt.Graphics;

public class Ball extends MovableObject {

    /**
     * Constructor để khởi tạo Ball.
     * @param x x
     * @param y y
     * @param size size bóng
     * @param dx tốc độ theo chiều ngang
     * @param dy tốc độ theo chiều dọc
     */
    public Ball(int x, int y, int size, int dx, int dy) {
        super(x, y, size, size, dx, dy);
    }

    /**
     * Nảy lại theo chiều dọc.
     */
    public void bounceY() {
        dy = -dy;
    }

    /**
     * Nảy lại theo chiều ngang.
     */
    public void bounceX() {
        dx = -dx;
    }

    public void bounceOffPaddle(Paddle paddle) {
        // Chỉ nảy lên
        if (dy > 0) {
            dy = -dy;
        }
        // Chống kẹt: đặt lại vị trí bóng ngay trên mặt Paddle
        this.y = paddle.getY() - this.height;
    }

    @Override
    public void update() {
        this.move();
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
