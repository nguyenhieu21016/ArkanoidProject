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
     * @param hitPoints Độ cứng
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
        hitPoints--;
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
            if (hitPoints > 1) {
                g.setColor(Color.GRAY); // Gạch cứng
            } else {
                g.setColor(Color.ORANGE); // Gạch thường
            }
            g.fillRect(x, y, width, height);

            // Vẽ viền
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}
