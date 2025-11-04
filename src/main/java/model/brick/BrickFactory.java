package model.brick;

import java.util.Random;

/**
 * Factory tạo ra các đối tượng Brick theo xác suất hoặc theo mã kiểu.
 * Mục tiêu: gom logic khởi tạo gạch về một nơi duy nhất để dễ bảo trì/mở rộng.
 */
public final class BrickFactory {

    private BrickFactory() {
    }

    /**
     * Tạo gạch ngẫu nhiên dựa trên xác suất rỗng/thường/đặc biệt.
     * @param random bộ sinh ngẫu nhiên
     * @param x tọa độ X
     * @param y tọa độ Y
     * @param width chiều rộng gạch
     * @param height chiều cao gạch
     * @param emptyChance xác suất ô trống (0..1)
     * @param normalChance xác suất gạch thường (0..1), phần còn lại là gạch mạnh
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
            // 25% of normal bricks are power-up bricks (easier)
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
     * Tạo gạch dựa trên mã kiểu (0: rỗng, 1: thường, 2: mạnh).
     */
    public static Brick createFromPattern(int type, int x, int y, int width, int height) {
        return switch (type) {
            case 1 -> new NormalBrick(x, y, width, height);
            case 2 -> new StrongBrick(x, y, width, height);
            default -> null;
        };
    }

    /**
     * Tạo gạch dựa trên mã kiểu nhưng có ngẫu nhiên hoá: một phần gạch thường sẽ là PowerUpBrick
     * để phù hợp với hành vi khởi tạo ban đầu.
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
                // 25% cơ hội chuyển thành PowerUpBrick
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



