package model.powerup;

import model.GameManager;

public class MultiBallPowerUp extends PowerUp {

    private static final int SIZE = 20;
    private static final int FALL_SPEED = 3;

    public MultiBallPowerUp(int x, int y) {
        super(x, y, SIZE, FALL_SPEED);
    }

    @Override
    public void apply(GameManager gameManager) {
        consumed = true;
        gameManager.spawnExtraBalls(2);
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.MULTI;
    }
}







