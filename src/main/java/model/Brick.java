package model;

import java.awt.*;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    protected boolean destroyed;

    /**
     * Constructor để khởi tạo một Brick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     * @param hitPoints độ cứng
     */
    public Brick(int x, int y, int width, int height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.destroyed = false;
    }

    /**
     * Xử lí khi Brick bị va chạm.
     */
    public void takeHit() {
        // Giảm độ cứng của gạch khi bị bóng chạm
        hitPoints--;
        // Nếu độ cứng còn 0, đánh dấu gạch bị phá hủy
        if ((hitPoints <= 0)) {
            destroyed = true;
        }
    }

    /**
     * Kiểm tra xem Brick đã bị phá hủy chưa.
     * @return kết quả check
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    @Override
    public void update(){
    }

    @Override
    public void render(Graphics g) {
        if (!destroyed) {
            // Nếu độ cứng > 1, vẽ màu xám
            if (hitPoints > 1) {
                g.setColor(Color.GRAY); // Gạch cứng
            } else {
                // Nếu độ cứng = 1, vẽ màu cam
                g.setColor(Color.ORANGE); // Gạch thường
            }
            // Vẽ hình chữ nhật đại diện
            g.fillRect(x, y, width, height);

            // Vẽ viền đen quanh gạch
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}
