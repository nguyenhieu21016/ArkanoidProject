package model;

import java.awt.Rectangle;

public class Ball extends MovableObject {
    private boolean launched = false;

    private static final double PADDLE_MOMENTUM_FACTOR = 0.2;
    private static final double MAX_BOUNCE_ANGLE_DEGREES = 75;
    private static final int MIN_VERTICAL_SPEED = -1;
    private static final int COLLISION_OFFSET = 5;

    public Ball(int x, int y, int size, int dx, int dy) {
        super(x, y, size, size, dx, dy);
    }

    public void bounceY() {
        dy = -dy;
    }

    public void bounceX() {
        dx = -dx;
    }

    public void calculateBounceFromPaddle(Paddle paddle) {
        this.y = paddle.getY() - this.height - 1;

        double ballCenter = this.x + this.width / 2.0;
        double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
        double relativeIntersectX = ballCenter - paddleCenter;
        double normalized = relativeIntersectX / (paddle.getWidth() / 2.0);

        double maxBounceAngle = Math.toRadians(MAX_BOUNCE_ANGLE_DEGREES);
        double bounceAngle = normalized * maxBounceAngle;

        double currentSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
        double newDx = currentSpeed * Math.sin(bounceAngle);
        double newDy = -currentSpeed * Math.cos(bounceAngle);

        newDx += PADDLE_MOMENTUM_FACTOR * paddle.getDx();

        dx = (int) Math.round(newDx);
        dy = (int) Math.round(newDy);

        if (this.dy == 0) {
            this.dy = MIN_VERTICAL_SPEED;
        }
    }

    public boolean handleCollisionWith(Brick brick) {
        if (!this.getBounds().intersects(brick.getBounds())) {
            return false;
        }

        Rectangle ballBounds = this.getBounds();
        Rectangle brickBounds = brick.getBounds();
        Rectangle intersection = ballBounds.intersection(brickBounds);
        if (intersection.isEmpty()) {
            return false;
        }

        if (intersection.width < intersection.height) {
            handleHorizontalCollision(brick, ballBounds, brickBounds);
        } else {
            handleVerticalCollision(brick, ballBounds, brickBounds);
        }

        brick.takeHit();
        this.move();
        return true;
    }

    private void handleHorizontalCollision(Brick brick, Rectangle ballBounds, Rectangle brickBounds) {
        this.bounceX();
        if (ballBounds.getCenterX() < brickBounds.getCenterX()) {
            this.x = brick.getX() - this.width - COLLISION_OFFSET;
        } else {
            this.x = brick.getX() + brick.getWidth() + COLLISION_OFFSET;
        }
    }

    private void handleVerticalCollision(Brick brick, Rectangle ballBounds, Rectangle brickBounds) {
        this.bounceY();
        if (ballBounds.getCenterY() < brickBounds.getCenterY()) {
            this.y = brick.getY() - this.height - COLLISION_OFFSET;
        } else {
            this.y = brick.getY() + brick.getHeight() + COLLISION_OFFSET;
        }
    }

    @Override
    public void update() {
        this.move();
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
