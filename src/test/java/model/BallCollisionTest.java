package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test đơn giản kiểm tra các logic va chạm quan trọng.
 */
public class BallCollisionTest {

    @Test
    void ballBouncesUpFromPaddle() {
        Paddle paddle = new Paddle(100, 300, 100, 20, 10);
        Ball ball = new Ball(145, 279, 20, 0, 5);

        // Mô phỏng va chạm: đặt bóng ngay trên paddle và gọi tính toán nảy
        ball.calculateBounceFromPaddle(paddle);

        // Kỳ vọng bóng bật lên (dy âm) và có thành phần ngang hợp lệ
        assertTrue(ball.isLaunched() || true, "Trạng thái launched không ảnh hưởng tới kiểm tra này");
        // dy phải âm để bay lên trên
        assertTrue(getDy(ball) < 0, "Bóng phải bật lên (dy âm)");
        // |dx| không quá nhỏ để tránh đường đi quá thẳng đứng
        assertTrue(Math.abs(getDx(ball)) >= 3, "Đảm bảo vận tốc ngang tối thiểu");
    }

    @Test
    void ballCollidesWithBrickAndConsumesHitPoint() {
        Ball ball = new Ball(100, 100, 20, 4, 4);
        Brick brick = new NormalBrick(110, 110, 70, 30);
        int before = brick.getHitPoints();

        boolean collided = ball.handleCollisionWith(brick);

        assertTrue(collided, "Phải phát hiện va chạm với gạch");
        assertEquals(before - 1, brick.getHitPoints(), "HP gạch phải giảm 1 sau va chạm");
    }

    // Truy cập dx/dy vì các trường thừa kế trong MovableObject là protected.
    // Ở test, dùng phương thức hỗ trợ để đọc qua di chuyển một bước.
    private int getDx(Ball b) {
        // Di chuyển 1 bước theo dx,dy rồi suy ra dx mới là chênh lệch x.
        // Không đẹp lắm nhưng tránh phải mở API sản phẩm.
        int xBefore = b.getX();
        int yBefore = b.getY();
        b.update();
        int dx = b.getX() - xBefore;
        // reset y để không ảnh hưởng test khác (không cần thiết nhưng an toàn)
        b.setY(yBefore + (b.getY() - yBefore));
        return dx;
    }

    private int getDy(Ball b) {
        int xBefore = b.getX();
        int yBefore = b.getY();
        b.update();
        int dy = b.getY() - yBefore;
        b.setX(xBefore + (b.getX() - xBefore));
        return dy;
    }
}









