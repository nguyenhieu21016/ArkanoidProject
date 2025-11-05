package model.brick;

import model.powerup.ExpandPaddlePowerUp;
import model.powerup.PowerUp;
import model.powerup.ExtraLifePowerUp;
import model.powerup.MultiBallPowerUp;
import model.powerup.MagnetPowerUp;

public class PowerUpBrick extends Brick {
    /**
     * Constructor để khởi tạo PowerUpBrick.
     * @param x x
     * @param y y
     * @param width chiều rộng
     * @param height chiều cao
     */
    public PowerUpBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);
    }

    /**
     * Random ra power-up tương ứng khi gạch bị vỡ
     * @return power-up rơi xuống
     */
    public PowerUp spawnPowerUp() {
        int centerX = getX() + getWidth() / 2 - 10;
        int y = getY() + getHeight();
        double r = Math.random();
        if (r < 0.40) {
            return new ExpandPaddlePowerUp(centerX, y);
        } else if (r < 0.65) {
            return new MultiBallPowerUp(centerX, y);
        } else if (r < 0.85) {
            return new MagnetPowerUp(centerX, y);
        } else {
            return new ExtraLifePowerUp(centerX, y);
        }
    }
}


