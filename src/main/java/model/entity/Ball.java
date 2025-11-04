package model.entity;

import model.brick.Brick;
import model.entity.Paddle;
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

    /**
     * Đảo hướng theo trục Y (bật lên/xuống).
     */
    public void bounceY() {
        dy = -dy;
    }

    /**
     * Đảo hướng theo trục X (bật trái/phải).
     */
    public void bounceX() {
        dx = -dx;
    }

    /**
     * Xử lý va tường trái: đẩy ra và đặt vận tốc ngang tối thiểu.
     */
    public void resolveLeftWallCollision() {
        setX(0);
        if (dx <= 0) {
            dx = Math.max(MIN_HORIZONTAL_SPEED, 3);
        }
    }

    /**
     * Xử lý va tường phải.
     * @param screenWidth độ rộng màn hình
     */
    public void resolveRightWallCollision(int screenWidth) {
            setX(screenWidth - getWidth());
        if (dx >= 0) {
            dx = -Math.max(MIN_HORIZONTAL_SPEED, 3);
        }
    }

    /**
     * Tính vận tốc bật lại khi chạm paddle dựa vào vị trí chạm.
     * @param paddle paddle
     */
    public void calculateBounceFromPaddle(Paddle paddle) {
        setY(paddle.getY() - getHeight() - 1);

        double ballCenterX = getX() + getWidth() / 2.0;
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

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);

        double newDx = currentSpeed * Math.sin(bounceAngle);
        double newDy = -currentSpeed * Math.cos(bounceAngle);

        dx = (int) Math.round(newDx);
        dy = (int) Math.round(newDy);
    }

    

    /**
     * Xử lý va chạm với gạch, cập nhật vị trí/vận tốc và trừ HP gạch.
     * @param brick gạch
     * @return true nếu có va chạm
     */
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
            setX(brick.getX() - getWidth() - COLLISION_OFFSET);
        } else {
            setX(brick.getX() + brick.getWidth() + COLLISION_OFFSET);
        }
    }

    private void handleVerticalCollision(Brick brick, Rectangle ballBounds, Rectangle brickBounds) {
        this.bounceY();
        if (ballBounds.getCenterY() < brickBounds.getCenterY()) {
            setY(brick.getY() - getHeight() - COLLISION_OFFSET);
        } else {
            setY(brick.getY() + brick.getHeight() + COLLISION_OFFSET);
        }
    }

    @Override
    /**
     * Cập nhật vị trí theo vận tốc (1 bước).
     */
    public void update() {
        this.move();
    }

    /**
     * Bóng đã được phóng chưa.
     */
    public boolean isLaunched() {
        return launched;
    }

    /**
     * Đặt trạng thái đã phóng.
     */
    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    /**
     * Phóng bóng với vận tốc cho trước.
     * @param dx vận tốc x
     * @param dy vận tốc y
     */
    public void launch(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        this.launched = true;
    }
}
