package model.powerup;

import model.manager.GameManager;
import model.entity.Paddle;

public class ExpandPaddlePowerUp extends PowerUp {
    private final double durationSeconds;
    private final double scaleFactor;

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 3);
        this.durationSeconds = 12.0;
        this.scaleFactor = 1.5;
    }

    @Override
    public void apply(GameManager gameManager) {
        if (consumed) return;
        Paddle paddle = gameManager.getPaddle();
        gameManager.applyExpandPaddleEffect(paddle, scaleFactor, durationSeconds);
        consumed = true;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.EXPAND;
    }
}


