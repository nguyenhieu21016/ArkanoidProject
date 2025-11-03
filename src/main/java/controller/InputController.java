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
        scene.setOnMouseMoved(event -> handleMouseMoved(event.getSceneX(), event.getSceneY()));
        scene.setOnMouseClicked(event -> handleMouseClicked(event.getSceneX(), event.getSceneY()));
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

    private void handleMouseMoved(double x, double y) {
        GameState state = gameManager.getCurrentState();
        if (state == GameState.MENU) {
            int index = hitTestMainMenu(x, y);
            if (index >= 0) {
                gameManager.getMenuState().setSelectedIndex(index);
            }
            gameManager.setBackHovered(false);
        } else if (state == GameState.PAUSED) {
            int index = hitTestPauseMenu(x, y);
            if (index >= 0) {
                gameManager.getPauseMenuState().setSelectedIndex(index);
            }
            gameManager.setBackHovered(hitTestBackButton(x, y));
        } else if (state == GameState.HIGHSCORE || state == GameState.INSTRUCTION) {
            gameManager.setBackHovered(hitTestBackButton(x, y));
        } else {
            gameManager.setBackHovered(false);
        }
    }

    private void handleMouseClicked(double x, double y) {
        GameState state = gameManager.getCurrentState();
        if (state == GameState.MENU) {
            int index = hitTestMainMenu(x, y);
            if (index >= 0) {
                gameManager.getMenuState().setSelectedIndex(index);
                executeMenuAction(gameManager.getMenuState().confirm());
            }
        } else if (state == GameState.HIGHSCORE || state == GameState.INSTRUCTION) {
            if (hitTestBackButton(x, y)) {
                gameManager.setCurrentState(GameState.MENU);
            }
        } else if (state == GameState.PAUSED) {
            if (hitTestBackButton(x, y)) {
                gameManager.resumeGame();
            } else {
                int index = hitTestPauseMenu(x, y);
                if (index >= 0) {
                    gameManager.getPauseMenuState().setSelectedIndex(index);
                    executePauseAction(gameManager.getPauseMenuState().confirm());
                }
            }
        }
    }

    // Hit test main menu options based on render layout in GameMenu
    private int hitTestMainMenu(double x, double y) {
        // Rendered at x ~200, y = 250 + i*60, font size ~28
        // Widen the horizontal and vertical hit area for better UX
        double left = 100;
        double right = 700;
        double topStart = 230;
        double rowHeight = 60;
        double rowTextHeight = 50;
        if (x < left || x > right) return -1;
        int idx = (int) Math.floor((y - topStart) / rowHeight);
        if (idx < 0) return -1;
        // tighten vertical bounds per row
        double rowTop = topStart + idx * rowHeight - rowTextHeight / 2.0;
        double rowBottom = topStart + idx * rowHeight + rowTextHeight / 2.0;
        if (y >= rowTop && y <= rowBottom && idx < gameManager.getMenuState().getOptions().length) {
            return idx;
        }
        return -1;
    }

    private boolean hitTestBackButton(double x, double y) {
        // Matches GameMenu back text at (40,60). Provide a generous clickable box.
        double left = 20;
        double top = 30;
        double right = 140;
        double bottom = 80;
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    private int hitTestPauseMenu(double x, double y) {
        // Pause menu options drawn at x ~240, y = 240 + i*50, font size ~26
        // Use generous bounds for better UX
        double left = 100;
        double right = 700;
        double topStart = 230;
        double rowHeight = 50;
        double rowTextHeight = 50;
        if (x < left || x > right) return -1;
        int idx = (int) Math.floor((y - topStart) / rowHeight);
        if (idx < 0) return -1;
        double rowTop = topStart + idx * rowHeight - rowTextHeight / 2.0;
        double rowBottom = topStart + idx * rowHeight + rowTextHeight / 2.0;
        if (y >= rowTop && y <= rowBottom && idx < gameManager.getPauseMenuState().getOptions().length) {
            return idx;
        }
        return -1;
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
            case UP -> gameManager.getPauseMenuState().moveUp();
            case DOWN -> gameManager.getPauseMenuState().moveDown();
            case ENTER -> executePauseAction(gameManager.getPauseMenuState().confirm());
            case ESCAPE -> gameManager.resumeGame();
            default -> {}
        }
    }

    private void executePauseAction(model.PauseMenuState.Action action) {
        switch (action) {
            case RESUME -> gameManager.resumeGame();
            case RESTART -> gameManager.startGame();
            case MAIN_MENU -> gameManager.setCurrentState(GameState.MENU);
            case EXIT -> System.exit(0);
            case NONE -> {}
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
