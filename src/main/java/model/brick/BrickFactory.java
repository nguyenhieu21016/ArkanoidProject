package model.brick;

import java.util.Random;

public final class BrickFactory {

    private BrickFactory() {
    }

    /**
     * Tạo gạch ngẫu nhiên dựa trên xác suất.
     * @param random random
     * @param x tọa độ X
     * @param y tọa độ Y
     * @param width chiều rộng gạch
     * @param height chiều cao gạch
     * @param emptyChance xác suất ô trống
     * @param normalChance xác suất gạch thường
     * @return Brick hoặc null nếu ô trống
     */
    public static Brick createRandomBrick(Random random,
                                          int x,
                                          int y,
                                          int width,
                                          int height,
                                          double emptyChance,
                                          double normalChance) {
        int r = random.nextInt(100);
        if (r < (int) (emptyChance * 100)) {
            return null;
        } else if (r < (int) (normalChance * 100)) {
            // 25% gạch thường đc random thành power-up
            int pr = random.nextInt(100);
            if (pr < 25) {
                return new PowerUpBrick(x, y, width, height);
            }
            return new NormalBrick(x, y, width, height);
        } else {
            return new StrongBrick(x, y, width, height);
        }
    }

    /**
     * Tạo gạch dựa trên pattern.
     */
    public static Brick createFromPattern(int type, int x, int y, int width, int height) {
        return switch (type) {
            case 1 -> new NormalBrick(x, y, width, height);
            case 2 -> new StrongBrick(x, y, width, height);
            default -> null;
        };
    }

    /**
     * Tạo gạch dựa trên pattern nhưng có ngẫu nhiên.
     */
    public static Brick createFromPatternRandomized(Random random,
                                                   int type,
                                                   int x,
                                                   int y,
                                                   int width,
                                                   int height) {
        switch (type) {
            case 0:
                return null;
            case 1: {
                // 25% cơ hội biến thành PowerUpBrick
                if (random.nextInt(100) < 25) {
                    return new PowerUpBrick(x, y, width, height);
                }
                return new NormalBrick(x, y, width, height);
            }
            case 2:
                return new StrongBrick(x, y, width, height);
            default:
                return null;
        }
    }
}



