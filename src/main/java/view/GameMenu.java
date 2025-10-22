package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameMenu {
    private int selectedIndex = 0;
    private final String[] options = {"Start", "High Score", "Instruction", "Exit"};

    public enum Action { NONE, START, HIGHSCORE, INSTRUCTION, EXIT }

    /**
     * Render chính
     * @param gc bút
     */
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 36));
        gc.fillText("ArkanoidProject", 150, 150);

        gc.setFont(new Font("Consolas", 28));
        for (int i = 0; i < options.length; i++) {
            if (i == selectedIndex) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.GRAY);
            }
            gc.fillText(options[i], 200, 250 + i * 60);
        }
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
