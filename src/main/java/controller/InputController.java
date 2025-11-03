package controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import model.GameManager;
import model.GameState;
import view.GameMenu;

public class InputController {

    private final GameManager gameManager;

    /**
     * Hàm khởi tạo lớp InputController.
     * @param gameManager gameManager.
     */
    public InputController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Gắn event bàn phím cho Scene nghe thao tác từ player.
     * @param scene scene.
     */
    public void listenTo(Scene scene) {
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event.getCode());
        });
        scene.setOnKeyReleased(event -> {
            handleKeyReleased(event.getCode());
        });
    }

    /**
     * Xử lý event khi player nhấn phím.
     * @param code code.
     */
    private void handleKeyPressed(KeyCode code) {
        GameState currentState = gameManager.getCurrentState();

        // Xử lý nhập tên khi ở trạng thái NAME_INPUT
        if (currentState == GameState.NAME_INPUT) {
            if (code == KeyCode.ENTER) {
                // Lưu điểm và chuyển sang GAME_OVER
                gameManager.saveHighScore();
                return;
            } else if (code == KeyCode.BACK_SPACE) {
                // Xóa ký tự cuối
                String currentName = gameManager.getCurrentPlayerName();
                if (!currentName.isEmpty()) {
                    gameManager.setCurrentPlayerName(currentName.substring(0, currentName.length() - 1));
                }
                return;
            } else {
                // Xử lý nhập ký tự (chỉ chấp nhận chữ cái, số và khoảng trắng, tối đa 20 ký tự)
                String keyText = code.getName();
                String currentName = gameManager.getCurrentPlayerName();
                if (keyText.length() == 1 && currentName.length() < 20) {
                    char c = keyText.charAt(0);
                    if (Character.isLetterOrDigit(c) || c == ' ') {
                        gameManager.setCurrentPlayerName(currentName + c);
                    }
                }
                return;
            }
        }

        if (currentState == GameState.MENU ||
                currentState == GameState.GAME_OVER ||
                currentState == GameState.GAME_WON) {
            if (code == KeyCode.SPACE) {
                gameManager.startGame();
            }

        }

        // Xử lý phím khi đang ở menu
        if (currentState == GameState.MENU) {
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

        // Xử lý phím khi đang xem bảng điểm
        if (currentState == GameState.HIGHSCORE) {
            switch (code) {
                case ESCAPE -> gameManager.setCurrentState(GameState.MENU);
                default -> {}
            }
        }

        // Xử lý phím khi đang xem hướng dẫn
        if (currentState == GameState.INSTRUCTION) {
            switch (code) {
                case ESCAPE -> gameManager.setCurrentState(GameState.MENU);
                default -> {}
            }
        }

        // Xử lý phím trong lúc chơi game
        if (currentState == GameState.RUNNING) {
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
                    break;
                case SPACE:
                    if (!gameManager.getBall().isLaunched()) {
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
        // Xử lý phím khi tạm dừng
        if (currentState == GameState.PAUSED) {
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

    /**
     * Xử lý sự kiện khi player thả phím.
     * @param code code.
     */
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
