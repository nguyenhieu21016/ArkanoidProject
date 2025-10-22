package model;

public class StrongBrick extends Brick {
    private static final int S_INITIAL_HITS = 2;

    /**
     * Contructor để khởi tạo StrongBrick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     */
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height, S_INITIAL_HITS);
    }
}
