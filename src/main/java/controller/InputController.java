package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.GameManager;

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
        if (gameManager.isGameOver() || gameManager.isGameWon()) {
            return;
        }

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
