package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FloatingText {
    private double x, y;
    private  String text;
    private double opacity = 1.0;
    private double dy = -0.8;
    private boolean active = true;

    public FloatingText(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public void update() {
        y += dy;
        opacity -= 0.02;
        if (opacity <= 0) {
            active = false;
        }
    }

    public void render(GraphicsContext gc) {
        gc.setGlobalAlpha(opacity);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("m6x11", 25));
        gc.fillText(text, x, y);
        gc.setGlobalAlpha(1.0);
    }

    public boolean isActive() {
        return active;
    }

}
