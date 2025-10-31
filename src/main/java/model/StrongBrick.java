package model;

import util.AssetManager;
import javafx.scene.image.Image;
import java.awt.*;

public class StrongBrick extends Brick {
    private static final int S_INITIAL_HITS = 2;
    private Image crackedImage;
    private boolean showCracked = false;

    /**
     * Contructor để khởi tạo StrongBrick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     */
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height, S_INITIAL_HITS);
        AssetManager assetManager = AssetManager.getInstance();
        crackedImage = assetManager.getImage("strong_brick_cracked");
    }
    @Override
    public void takeHit() {
        hitPoints--;
        if (hitPoints == 1) {
            showCracked = true;
        } else if (hitPoints <= 0) {
            destroyed = true;
        }
    }
}
