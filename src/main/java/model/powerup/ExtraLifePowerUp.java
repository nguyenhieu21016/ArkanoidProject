package model.powerup;

import model.manager.GameManager;

public class ExtraLifePowerUp extends PowerUp {
    private final int livesToAdd;

    public ExtraLifePowerUp(int x, int y) {
        super(x, y, 20, 3);
        this.livesToAdd = 1;
    }

    @Override
    public void apply(GameManager gameManager) {
        if (consumed) return;
        gameManager.addLife(livesToAdd);
        consumed = true;
    }

    @Override
    public PowerUpType getType() {
        return PowerUpType.EXTRA_LIFE;
    }
}


