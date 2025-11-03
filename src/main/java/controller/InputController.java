package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import model.GameManager;
import model.GameState;
import model.MenuState;

public class InputController {

    private final GameManager gameManager;

    private static final int MAX_PLAYER_NAME_LENGTH = 20;
    private static final int BALL_LAUNCH_SPEED_X = 5;
    private static final int BALL_LAUNCH_SPEED_Y = 5;

    public InputController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

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
        }
    }

    private void handleNameInput(KeyCode code) {
        if (code == KeyCode.ENTER) {
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
            case UP -> menuState.moveUp();
            case DOWN -> menuState.moveDown();
            case ENTER -> executeMenuAction(menuState.confirm());
        }
    }

    private void executeMenuAction(MenuState.Action action) {
        switch (action) {
            case START -> gameManager.startGame();
            case EXIT -> System.exit(0);
            case HIGHSCORE -> gameManager.setCurrentState(GameState.HIGHSCORE);
            case INSTRUCTION -> gameManager.setCurrentState(GameState.INSTRUCTION);
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
        }
    }

    private void launchBall() {
        if (!gameManager.getBall().isLaunched()) {
            int dx = Math.random() < 0.5 ? -BALL_LAUNCH_SPEED_X : BALL_LAUNCH_SPEED_X;
            gameManager.getBall().launch(dx, -BALL_LAUNCH_SPEED_Y);
        }
    }

    private void handlePausedInput(KeyCode code) {
        switch (code) {
            case P -> gameManager.resumeGame();
            case R -> gameManager.startGame();
        }
    }

    private void handleGameOverInput(KeyCode code) {
        if (code == KeyCode.SPACE) {
            gameManager.startGame();
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
