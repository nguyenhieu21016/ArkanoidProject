package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FloatingText {
    private double x, y;
    private String text;
    private double opacity = 1.0;
    private double dy = -0.8;
    private boolean active = true;
    private Color color = Color.WHITE;
    private double fontSize = 25;
    private double fadeSpeed = 0.02;

    /**
     * Khởi tạo một đối tượng FloatingText.
     * @param x tọa độ X của text
     * @param y tọa độ Y của text
     * @param text nội dung cần hiển thị
     */
    public FloatingText(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public FloatingText(double x, double y, String text, Color color, double fontSize, double dy, double fadeSpeed) {
        this.x = x;
        this.y = y;
        this.text = text;
        if (color != null) this.color = color;
        if (fontSize > 0) this.fontSize = fontSize;
        this.dy = dy;
        this.fadeSpeed = fadeSpeed;
    }

    /**
     * Cập nhật vị trí và độ mờ của text theo thời gian.
     */
    public void update() {
        // Di chuyển text lên trên
        y += dy;
        // Giảm độ mờ theo thời gian
        opacity -= fadeSpeed;
        if (opacity <= 0) {
            // Ẩn text khi đã mờ hết
            active = false;
        }
    }

    /**
     * Vẽ text lên màn hình.
     * @param gc gc
     */
    public void render(GraphicsContext gc) {
        // Thiết lập độ trong suốt cho text
        gc.setGlobalAlpha(opacity);
        gc.setFill(color);
        gc.setFont(Font.font("m6x11", fontSize));
        gc.fillText(text, x, y);
        // Khôi phục lại độ trong suốt bình thường
        gc.setGlobalAlpha(1.0);
    }

    /**
     * Kiểm tra text còn hiển thị hay đã biến mất.
     * @return true nếu text vẫn còn hiển thị, ngược lại false.
     */
    public boolean isActive() {
        return active;
    }

}
