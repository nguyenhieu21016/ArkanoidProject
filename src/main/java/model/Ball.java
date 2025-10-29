package model;

import java.awt.*;

public class Ball extends MovableObject {

    private boolean launched = false;

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
        this.y = paddle.getY() - this.height - 1;

        // Tính tâm của paddle và bóng
        double ballCenter = this.x + this.width / 2.0;
        double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;

        // Tính độ lệch tương đối
        double relativeIntersectX = ballCenter - paddleCenter;
        double normalized = relativeIntersectX / (paddle.getWidth() / 2.0);

        // Chọn góc bật
        double maxBounceAngle = Math.toRadians(75);
        double bounceAngle = normalized * maxBounceAngle;

        // Tính tốc độ tổng hiện tại
        double currenTotalSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
        double newDx = currenTotalSpeed * Math.sin(bounceAngle);
        double newDy = -currenTotalSpeed * Math.cos(bounceAngle);

        // Momentum cho paddle
        newDx += 0.2 * paddle.getDx();

        dx = (int) Math.round(newDx);
        dy = (int) Math.round(newDy);

        // Đảm bảo không nảy ngang
        if (this.dy == 0) {
            this.dy = -1;
        }

    }

    public boolean handleCollisionWith(Brick brick) {
        // Không va chạm -> trả về false
        if (!this.getBounds().intersects(brick.getBounds())) {
            return false;
        }

        Rectangle ballR = this.getBounds();
        Rectangle brickR = brick.getBounds();
        Rectangle inter = ballR.intersection(brickR);
        if (inter.isEmpty()) {
            return false;
        }

        if (inter.width < inter.height) {
            this.bounceX();
            if (ballR.getCenterX() < brickR.getCenterX()) {
                this.x = brick.getX() - this.width - 5;
            } else {
                this.x = brick.getX() + brick.getWidth() + 5;
            }
        } else {
            this.bounceY();
            if (ballR.getCenterY() < brickR.getCenterY()) {
                this.y = brick.getY() - this.height - 5;
            } else {
                this.y = brick.getY() + brick.getHeight() + 5;
            }
        }
        brick.takeHit();
        this.move();
        return true;


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

    public boolean isLaunched() {
        return launched;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    public void launch(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        this.launched = true;
    }
}
