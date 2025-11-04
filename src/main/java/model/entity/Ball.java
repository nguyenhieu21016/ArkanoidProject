package model;

import java.awt.Rectangle;

public class Ball extends MovableObject {
    private boolean launched = false;

    private static final double MAX_BOUNCE_ANGLE_DEGREES = 60;
    private static final double MIN_BOUNCE_ANGLE_DEGREES = 20;
    private static final int MIN_HORIZONTAL_SPEED = 2;
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

    /**
     * Đảm bảo sau khi va vào tường trái, bóng được đẩy ra khỏi tường và có vận tốc ngang tối thiểu.
     */
    public void resolveLeftWallCollision() {
        this.x = 0;
        if (dx <= 0) {
            dx = Math.max(MIN_HORIZONTAL_SPEED, 3);
        }
    }

    /**
     * Đảm bảo sau khi va vào tường phải, bóng được đẩy ra khỏi tường và có vận tốc ngang tối thiểu.
     * @param screenWidth độ rộng màn hình
     */
    public void resolveRightWallCollision(int screenWidth) {
        this.x = screenWidth - this.width;
        if (dx >= 0) {
            dx = -Math.max(MIN_HORIZONTAL_SPEED, 3);
        }
    }

    public void calculateBounceFromPaddle(Paddle paddle) {
        this.y = paddle.getY() - this.height - 1;

        double ballCenterX = this.x + this.width / 2.0;
        double paddleLeft = paddle.getX();
        
        double hitPosition = (ballCenterX - paddleLeft) / paddle.getWidth();
        hitPosition = Math.max(0.0, Math.min(1.0, hitPosition));

        double normalizedHitPosition = (hitPosition - 0.5) * 2.0;
        
        double minAngle = Math.toRadians(MIN_BOUNCE_ANGLE_DEGREES);
        double maxAngle = Math.toRadians(MAX_BOUNCE_ANGLE_DEGREES);
        double bounceAngle = minAngle + (maxAngle - minAngle) * Math.abs(normalizedHitPosition);
        
        if (normalizedHitPosition < 0) {
            bounceAngle = -bounceAngle;
        }

        double currentSpeed = Math.sqrt(this.dx * this.dx + this.dy * this.dy);

        double newDx = currentSpeed * Math.sin(bounceAngle);
        double newDy = -currentSpeed * Math.cos(bounceAngle);

        dx = (int) Math.round(newDx);
        dy = (int) Math.round(newDy);
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
