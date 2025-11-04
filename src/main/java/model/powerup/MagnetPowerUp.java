package model.powerup;

import model.GameManager;

public class MagnetPowerUp extends PowerUp {
    private final double durationSeconds;

    public MagnetPowerUp(int x, int y) {
        super(x, y, 20, 3);
        this.durationSeconds = 8.0;
    }

    @Override
    public void apply(GameManager gameManager) {
        if (consumed) return;
        gameManager.activateMagnet(durationSeconds);
        consumed = true;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.MAGNET;
    }
}






