package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.manager.GameManager;
import model.manager.HighScoreManager;
import model.state.MenuState;
import model.state.PauseMenuState;
import model.state.SettingsState;
import util.AssetManager;

public class GameMenu {

    private static final double OVERLAY_OPACITY = 0.5;

    public void render(GraphicsContext gc, MenuState menuState) {
        renderBackgroundAndOverlay(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 50));
        gc.fillText("ArkanoidProject", 150, 150);

        String[] options = menuState.getOptions();
        int selectedIndex = menuState.getSelectedIndex();
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
        renderBackgroundAndOverlay(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 32));
        gc.fillText("HIGH SCORES", 250, 100);

        var scores = HighScoreManager.getInstance().getScores();
        gc.setFont(new Font("m6x11", 24));
        for (int i = 0; i < scores.size(); i++) {
            var entry = scores.get(i);
            gc.fillText((i + 1) + ". " + entry.getName() + " - " + entry.getScore(), 220, 160 + i * 35);
        }

        gc.setFont(new Font("m6x11", 22));
        gc.setFill(Color.GRAY);
        gc.fillText("Back", 40, 60);
        gc.setFont(new Font("m6x11", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to return", 260, 550);
    }

    public void renderInstruction(GraphicsContext gc) {
        renderBackgroundAndOverlay(gc);

        // Tiêu đề hướng dẫn
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 40));
        gc.fillText("HOW TO PLAY", 250, 100);

        gc.setFont(new Font("m6x11", 22));
        int y = 180;
        gc.fillText("A  D : Move paddle left/right", 180, y);
        gc.fillText("SPACE : Launch the ball", 180, y + 40);
        gc.fillText("P : Pause / Resume game", 180, y + 80);
        gc.fillText("Break all bricks to win!", 180, y + 120);
        gc.fillText("Don't let the ball fall!", 180, y + 160);

        gc.setFont(new Font("m6x11", 22));
        gc.setFill(Color.GRAY);
        gc.fillText("Back", 40, 60);
        gc.setFont(new Font("m6x11", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to return", 260, 550);
    }

    public void renderPause(GraphicsContext gc, PauseMenuState pauseState) {
        renderBackgroundAndOverlay(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 36));
        gc.fillText("PAUSED", 300, 150);

        // BACK button
        gc.setFont(new Font("m6x11", 22));
        gc.setFill(Color.GRAY);
        gc.fillText("Back", 40, 60);

        String[] options = pauseState.getOptions();
        int selectedIndex = pauseState.getSelectedIndex();

        gc.setFont(new Font("m6x11", 26));
        for (int i = 0; i < options.length; i++) {
            String label = options[i];
            if (i == selectedIndex) {
                gc.setFill(Color.web("#B8F4DC"));
            } else {
                gc.setFill(Color.GRAY);
            }
            gc.fillText(label, 240, 240 + i * 50);
        }

        gc.setFont(new Font("m6x11", 18));
        gc.setFill(Color.WHITE);
        gc.fillText("Use UP/DOWN to navigate, ENTER to confirm", 180, 520);
        gc.fillText("Press P to resume", 300, 550);
    }

    public void renderSettings(GraphicsContext gc, SettingsState settingsState) {
        renderBackgroundAndOverlay(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 40));
        gc.fillText("SETTINGS", 280, 120);

        // Back button
        gc.setFont(new Font("m6x11", 22));
        gc.setFill(Color.GRAY);
        gc.fillText("Back", 40, 60);

        String[] options = settingsState.getOptions();
        int selectedIndex = settingsState.getSelectedIndex();

        gc.setFont(new Font("m6x11", 26));
        for (int i = 0; i < options.length; i++) {
            String label = options[i];
            boolean isSelected = (i == selectedIndex);

            if (isSelected) {
                gc.setFill(Color.web("#B8F4DC"));
            } else {
                gc.setFill(Color.GRAY);
            }

            // Hiển thị label
            gc.fillText(label, 200, 220 + i * 60);

            // Hiển thị volume values (chỉ text, không có progress bar)
            if (i < 2) {
                double volume = (i == 0) ? settingsState.getMasterVolume() : settingsState.getSfxVolume();
                int volumePercent = (int) Math.round(volume * 100);

                // Volume value
                gc.setFill(Color.WHITE);
                gc.setFont(new Font("m6x11", 22));
                gc.fillText(volumePercent + "%", 500, 220 + i * 60);

                // Reset font
                gc.setFont(new Font("m6x11", 26));
            }
        }

        // Instructions
        gc.setFont(new Font("m6x11", 18));
        gc.setFill(Color.WHITE);
        gc.fillText("Use UP/DOWN to navigate, LEFT/RIGHT or A/D to adjust volume", 120, 520);
        gc.fillText("Press ESC to return", 280, 550);
    }

    private void renderBackgroundAndOverlay(GraphicsContext gc) {
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        gc.setFill(Color.color(0, 0, 0, OVERLAY_OPACITY));
        gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
    }
}
