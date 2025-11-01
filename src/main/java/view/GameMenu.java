package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.GameManager;
import util.AssetManager;

public class GameMenu {
    private int selectedIndex = 0;
    private final String[] options = {"Start", "High Score", "Instruction", "Exit"};

    public enum Action { NONE, START, HIGHSCORE, INSTRUCTION, EXIT }

    /**
     * Render chính
     * @param gc bút
     */
    public void render(GraphicsContext gc) {
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 36));
        gc.fillText("ArkanoidProject", 150, 150);

        gc.setFont(new Font("m6x11", 28));
        for (int i = 0; i < options.length; i++) {
            if (i == selectedIndex) {
                gc.setFill(Color.web("#B8F4DC"));
            } else {
                gc.setFill(Color.GRAY);
            }
            gc.fillText(options[i], 200, 250 + i * 60);
        }
    }

    public void renderHighScores(GraphicsContext gc) {
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 32));
        gc.fillText("HIGH SCORES", 250, 100);

        var scores = model.HighScoreManager.getInstance().getScores();
        gc.setFont(new Font("m6x11", 24));
        for (int i = 0; i < scores.size(); i++) {
            var s = scores.get(i);
            gc.fillText((i + 1) + ". " + s.getName() + " - " + s.getScore(), 220, 160 + i * 35);
        }

        gc.setFont(new Font("m6x11", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to return", 260, 500);
    }
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    public Action confirm() {
        return switch (selectedIndex) {
            case 0 -> Action.START;
            case 1 -> Action.HIGHSCORE;
            case 2 -> Action.INSTRUCTION;
            case 3 -> Action.EXIT;
            default -> Action.NONE;
        };
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
