package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import model.manager.GameManager;
import model.state.GameState;
import model.state.MenuState;
import model.state.PauseMenuState;
import model.state.SettingsState;
import util.SoundManager;

public class InputController {

    private final GameManager gameManager;

    private static final int MAX_PLAYER_NAME_LENGTH = 20;
    private static final int BALL_LAUNCH_SPEED_X = 5;
    private static final int BALL_LAUNCH_SPEED_Y = 5;

    public InputController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Đăng ký lắng nghe phím cho Scene.
     * @param scene Scene cần gắn
     */
    public void listenTo(Scene scene) {
        scene.setOnKeyPressed(event -> handleKeyPressed(event.getCode()));
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));
    }

    private void handleKeyPressed(KeyCode code) {
        GameState currentState = gameManager.getCurrentState();

        switch (currentState) {
            case NAME_INPUT -> handleNameInput(code);
            case MENU -> handleMenuInput(code);
            case HIGHSCORE, INSTRUCTION -> handleNavigationInput(code);
            case RUNNING -> handleGameInput(code);
            case PAUSED -> handlePausedInput(code);
            case GAME_OVER, GAME_WON -> handleGameOverInput(code);
            case SETTINGS -> handleSettingsInput(code);
        }
    }

    private void handleNameInput(KeyCode code) {
        if (code == KeyCode.ENTER) {
            SoundManager.getInstance().playSound("selected");
            gameManager.saveHighScore();
        } else if (code == KeyCode.BACK_SPACE) {
            String currentName = gameManager.getCurrentPlayerName();
            if (!currentName.isEmpty()) {
                gameManager.setCurrentPlayerName(currentName.substring(0, currentName.length() - 1));
            }
        } else {
            handleNameInputCharacter(code);
        }
    }

    private void handleNameInputCharacter(KeyCode code) {
        String keyText = code.getName();
        String currentName = gameManager.getCurrentPlayerName();
        if (keyText.length() == 1 && currentName.length() < MAX_PLAYER_NAME_LENGTH) {
            char c = keyText.charAt(0);
            if (Character.isLetterOrDigit(c) || c == ' ') {
                gameManager.setCurrentPlayerName(currentName + c);
            }
        }
    }

    private void handleMenuInput(KeyCode code) {
        MenuState menuState = gameManager.getMenuState();
        switch (code) {
            case UP -> {
                menuState.moveUp();
                SoundManager.getInstance().playSound("optionChange");
            }
            case DOWN -> {
                menuState.moveDown();
                SoundManager.getInstance().playSound("optionChange");
            }
            case ENTER -> executeMenuAction(menuState.confirm());
            default -> {}
        }
    }


    private void executeMenuAction(MenuState.Action action) {
        if (action != MenuState.Action.NONE) {
            SoundManager.getInstance().playSound("selected");
        }
        switch (action) {
            case START -> gameManager.startGame();
            case EXIT -> System.exit(0);
            case HIGHSCORE -> gameManager.setCurrentState(GameState.HIGHSCORE);
            case INSTRUCTION -> gameManager.setCurrentState(GameState.INSTRUCTION);
            case SETTINGS -> gameManager.setCurrentState(GameState.SETTINGS);
            case NONE -> {}
        }
    }

    private void handleNavigationInput(KeyCode code) {
        if (code == KeyCode.ESCAPE) {
            gameManager.setCurrentState(GameState.MENU);
        }
    }

    private void handleGameInput(KeyCode code) {
        switch (code) {
            case LEFT, A -> gameManager.getPaddle().setMovingLeft(true);
            case RIGHT, D -> gameManager.getPaddle().setMovingRight(true);
            case ESCAPE -> gameManager.setCurrentState(GameState.MENU);
            case SPACE -> launchBall();
            case P -> gameManager.pauseGame();
            default -> {}
        }
    }

    private void launchBall() {
        if (!gameManager.getBall().isLaunched()) {
            // Nếu bóng đang dính (magnet), launch thẳng lên trên
            if (gameManager.isMagnetActive()) {
                gameManager.getBall().launch(0, -BALL_LAUNCH_SPEED_Y);
            } else {
                int dx = Math.random() < 0.5 ? -BALL_LAUNCH_SPEED_X : BALL_LAUNCH_SPEED_X;
                gameManager.getBall().launch(dx, -BALL_LAUNCH_SPEED_Y);
            }
        }
    }

    private void handlePausedInput(KeyCode code) {
        switch (code) {
            case P -> gameManager.resumeGame();
            case UP -> {
                gameManager.getPauseMenuState().moveUp();
                SoundManager.getInstance().playSound("optionChange");
            }
            case DOWN -> {
                gameManager.getPauseMenuState().moveDown();
                SoundManager.getInstance().playSound("optionChange");
            }
            case ENTER -> executePauseAction(gameManager.getPauseMenuState().confirm());
            case ESCAPE -> gameManager.resumeGame();
            default -> {}
        }
    }

    private void executePauseAction(PauseMenuState.Action action) {
        if (action != PauseMenuState.Action.NONE) {
            SoundManager.getInstance().playSound("selected");
        }
        switch (action) {
            case RESUME -> gameManager.resumeGame();
            case RESTART -> gameManager.startGame();
            case SETTINGS -> gameManager.setCurrentState(GameState.SETTINGS);
            case MAIN_MENU -> gameManager.setCurrentState(GameState.MENU);
            case EXIT -> System.exit(0);
            case NONE -> {}
        }
    }

    private void handleGameOverInput(KeyCode code) {
        if (code == KeyCode.SPACE) {
            SoundManager.getInstance().playSound("selected");
            gameManager.startGame();
        }
    }

    private void handleSettingsInput(KeyCode code) {
        SettingsState settingsState = gameManager.getSettingsState();
        switch (code) {
            case UP -> {
                settingsState.moveUp();
                SoundManager.getInstance().playSound("optionChange");
            }
            case DOWN -> {
                settingsState.moveDown();
                SoundManager.getInstance().playSound("optionChange");
            }
            case LEFT, A -> {
                settingsState.adjustVolumeLeft();
                applyVolumeSettings();
                SoundManager.getInstance().playSound("optionChange");
            }
            case RIGHT, D -> {
                settingsState.adjustVolumeRight();
                applyVolumeSettings();
                SoundManager.getInstance().playSound("optionChange");
            }
            case ENTER -> {
                SettingsState.Action action = settingsState.confirm();
                if (action == SettingsState.Action.BACK) {
                    SoundManager.getInstance().playSound("selected");
                    returnToPreviousState();
                }
            }
            case ESCAPE -> returnToPreviousState();
            default -> {}
        }
    }

    private void applyVolumeSettings() {
        SettingsState settingsState = gameManager.getSettingsState();
        util.SoundManager soundManager = util.SoundManager.getInstance();
        soundManager.setMasterVolume(settingsState.getMasterVolume());
        soundManager.setSfxVolume(settingsState.getSfxVolume());
        // SFX volume có thể được áp dụng riêng nếu cần
        gameManager.refreshMusicVolume();
    }

    private void returnToPreviousState() {
        GameState previousState = gameManager.getPreviousState();
        if (previousState == GameState.PAUSED) {
            gameManager.setCurrentState(GameState.PAUSED);
        } else {
            gameManager.setCurrentState(GameState.MENU);
        }
    }

    private void handleKeyReleased(KeyCode code) {
        GameState currentState = gameManager.getCurrentState();

        if (currentState == GameState.RUNNING) {
            switch (code) {
                case LEFT:
                case A:
                    gameManager.getPaddle().setMovingLeft(false);
                    break;
                case RIGHT:
                case D:
                    gameManager.getPaddle().setMovingRight(false);
                    break;
                default:
                    break;
            }
        }
    }
}
