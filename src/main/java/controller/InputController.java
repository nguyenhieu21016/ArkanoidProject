package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import model.GameManager;
import model.GameState;
import view.GameMenu;

public class InputController {

    private final GameManager gameManager;

    /**
     * Constructor để khởi tạo InputController.
     * @param gameManager gameManager
     */
    public InputController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Cho Scene nghe event.
     * @param scene
     */
    public void listenTo(Scene scene) {
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        scene.setOnKeyReleased(event -> {
            handleKeyReleased(event.getCode());
        });
    }

    private void handleKeyPressed(KeyCode code) {
        GameState currenState = gameManager.getCurrentState();

        if (currenState == GameState.MENU ||
            currenState == GameState.GAME_OVER ||
            currenState == GameState.GAME_WON) {
            if (code == KeyCode.SPACE) {
                gameManager.startGame();
            }

        }

        if (currenState == GameState.MENU) {
            switch (code) {
                case UP ->  gameManager.getMenu().moveUp();
                case DOWN ->  gameManager.getMenu().moveDown();
                case ENTER -> {
                    GameMenu.Action act = gameManager.getMenu().confirm();
                    switch (act) {
                        case START -> gameManager.startGame();
                        case EXIT -> System.exit(0);
                        case HIGHSCORE -> gameManager.setCurrentState(GameState.HIGHSCORE);
                        case INSTRUCTION -> gameManager.setCurrentState(GameState.INSTRUCTION);
                        default -> {}
                    }
                }
                default -> {}
            }
        }

        if (currenState == GameState.HIGHSCORE) {
            switch (code) {
                case ESCAPE -> gameManager.setCurrentState(GameState.MENU);
                default -> {}
            }
        }

        if (currenState == GameState.INSTRUCTION) {
            switch (code) {
                case ESCAPE -> gameManager.setCurrentState(GameState.MENU);
                default -> {}
            }
        }

        if (currenState == GameState.RUNNING) {
            switch (code) {
                case LEFT:
                case A:
                    gameManager.getPaddle().setMovingLeft(true);
                    break;
                case RIGHT:
                case D:
                    gameManager.getPaddle().setMovingRight(true);
                    break;
                case ESCAPE:
                    gameManager.setCurrentState(GameState.MENU);
                case SPACE:
                    if(!gameManager.getBall().isLaunched()) {
                        int initDx = Math.random() < 0.5 ? -5 : 5;
                        int initDy = -5;
                        gameManager.getBall().launch(initDx, initDy);
                    }
                    break;
                case P:
                        gameManager.pauseGame();
                    break;
            }
        }
        if (currenState == GameState.PAUSED) {
            switch (code) {
                case P:
                    gameManager.resumeGame();
                    break;
                case R:
                    gameManager.startGame();
                    break;
            }
        }
    }

    private void handleKeyReleased(KeyCode code) {
        GameState currenState = gameManager.getCurrentState();

        if (currenState == GameState.RUNNING) {
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
