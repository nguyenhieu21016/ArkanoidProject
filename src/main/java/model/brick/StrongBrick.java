package model.brick;

public class StrongBrick extends Brick {
    private static final int INITIAL_HIT_POINTS = 2;

    /**
     * Constructor để khởi tạo StrongBrick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     */
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height, INITIAL_HIT_POINTS);
    }
    @Override
    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            destroyed = true;
        }
    }
}
