package model;

import java.awt.Color;
import java.awt.Graphics;

public class Paddle extends MovableObject {
    private int moveSpeed;

    /**
     * Constructor để khởi tạo Paddle.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     * @param moveSpeed tốc độ
     */
    public Paddle(int x, int y, int width, int height, int moveSpeed) {
        super(x, y, width, height, 0, 0);
        this.moveSpeed = moveSpeed;
    }

    /**
     * Di chuyển paddle sang trái.
     */
    public void moveLeft() {
        x -= moveSpeed;
    }

    /**
     * Di chuyển paddle sang phải.
     */
    public void moveRight() {
        x += moveSpeed;
    }

    /**
     * Kiểm tra va chạm cạnh màn hình.
     * @param screenWidth độ rộng màn hình
     */
    public void update(int screenWidth) {
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
