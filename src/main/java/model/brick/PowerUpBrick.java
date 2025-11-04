package model;

import model.powerup.ExpandPaddlePowerUp;
import model.powerup.PowerUp;
import model.powerup.ExtraLifePowerUp;
import model.powerup.MultiBallPowerUp;
import model.powerup.MagnetPowerUp;

public class PowerUpBrick extends Brick {
    public PowerUpBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);
    }

    public PowerUp spawnPowerUp() {
        int centerX = this.x + this.width / 2 - 10;
        int y = this.y + this.height;
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


