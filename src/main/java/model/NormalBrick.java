package model;

public class NormalBrick extends Brick {
    private static final int N_INITIAL_HITS = 1;
    /**
     * Constructor để khởi tạo NormalBrick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     */
    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height, N_INITIAL_HITS);
    }
}
