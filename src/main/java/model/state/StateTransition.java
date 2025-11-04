package model.state;

public class StateTransition {

    private boolean active = false;
    private GameState fromState;
    private GameState toState;
    private double timer = 0.0;
    private double duration = 0.4;
    private boolean switched = false;

    public void start(GameState from, GameState to, double durationSeconds) {
        if (from == to) {
            active = false;
            return;
        }
        this.active = true;
        this.fromState = from;
        this.toState = to;
        this.timer = 0.0;
        this.duration = durationSeconds;
        this.switched = false;
    }

    public void update(double deltaSeconds) {
        if (!active) return;
        timer += deltaSeconds;
        if (!switched && timer >= duration / 2.0) {
            switched = true;
        }
        if (timer >= duration) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean shouldSwitchNow() {
        return active && switched;
    }

    public void markSwitchedHandled() {
        this.switched = false;
    }

    public double getProgress() {
        if (!active || duration <= 0) return 0.0;
        double p = timer / duration;
        return Math.max(0.0, Math.min(1.0, p));
    }

    public GameState getFromState() {
        return fromState;
    }

    public GameState getToState() {
        return toState;
    }
}










