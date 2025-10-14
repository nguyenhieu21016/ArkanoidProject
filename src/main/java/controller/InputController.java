package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import model.GameManager;
import model.GameState;

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

        if (currenState == GameState.RUNNING) {
            switch (code) {
                case LEFT:
                case A:
                    gameManager.getPaddle().moveLeft();
                    break;
                case RIGHT:
                case D:
                    gameManager.getPaddle().moveRight();
            }
        }
    }
}
