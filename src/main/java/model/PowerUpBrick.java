package model;

import model.powerup.ExpandPaddlePowerUp;
import model.powerup.PowerUp;
import model.powerup.ExtraLifePowerUp;

public class PowerUpBrick extends Brick {
    public PowerUpBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1);
    }

    public PowerUp spawnPowerUp() {
        int centerX = this.x + this.width / 2 - 10;
        int y = this.y + this.height;
        double r = Math.random();
        if (r < 0.7) {
            return new ExpandPaddlePowerUp(centerX, y);
        } else {
            return new ExtraLifePowerUp(centerX, y);
        }
    }
}


