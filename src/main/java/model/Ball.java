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

    public void calculateBounceFromPaddle(Paddle paddle) {
        // Chống kẹt bóng - reset bóng tại mặt paddle
        this.y = paddle.getY() - this.height;

        // Logic tính góc nảy
        double paddleWidth = paddle.getWidth();

        // Vị trí tương đối của tâm bóng so với mép trái paddle
        double ballCenterX = this.x + this.width / 2.0;
        double relativeHitPosition = ballCenterX - paddle.getX();

        // Tính tốc độ tổng hiện tại
        double currenTotalSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);

        // Nếu tốc độ đi quá thấp, đặt tốc độ tối thiểu
        final double MIN_SPEED = 4.0;
        if (currenTotalSpeed < MIN_SPEED) {
            currenTotalSpeed = MIN_SPEED;
        }

        // Chia paddle thành 5 phần
        double segmentWidth = paddleWidth / 5.0;

        double newDxNormalized = 0;
        double newDyNormalized = -1; // luôn đi lên

        if (relativeHitPosition < segmentWidth) {
            newDxNormalized = -0.8;
        } else if (relativeHitPosition < segmentWidth * 2) {
            newDxNormalized = -0.4;
        } else if (relativeHitPosition < segmentWidth * 3) {
            newDxNormalized = 0;
        } else if (relativeHitPosition < segmentWidth * 4) {
            newDxNormalized = 0.4;
        } else {
            newDxNormalized = 0.8;
        }

        // Chuẩn hóa vector hướng (vector đơn vị)
        double directionLength = Math.sqrt(newDxNormalized * newDxNormalized + newDyNormalized
        * newDyNormalized);
        newDxNormalized /= directionLength;
        newDyNormalized /= directionLength;

        this.dx = (int) Math.round(newDxNormalized * currenTotalSpeed);
        this.dy = (int) Math.round(newDyNormalized * currenTotalSpeed);

        // Đảm bảo không nảy ngang
        if (this.dy == 0) {
            this.dy = -1;
        }

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
