package model.powerup;

import model.manager.GameManager;
import model.entity.GameObject;

public abstract class PowerUp extends GameObject {
    protected int fallSpeed;
    protected boolean consumed = false;

    public PowerUp(int x, int y, int size, int fallSpeed) {
        super(x, y, size, size);
        this.fallSpeed = fallSpeed;
    }

    @Override
    public void update() {
        setY(getY() + fallSpeed);
    }

    public boolean isConsumed() {
        return consumed;
    }

    public abstract void apply(GameManager gameManager);

    public abstract PowerUpType getType();
}


