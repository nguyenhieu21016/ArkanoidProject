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

    /**
     * Các hành động có thể được chọn từ menu.
     */
    public enum Action { NONE, START, HIGHSCORE, INSTRUCTION, EXIT }

    /**
     * Vẽ giao diện menu chính.
     * @param gc đối tượng GraphicsContext dùng để vẽ.
     */
    public void render(GraphicsContext gc) {
        // Vẽ background của menu
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        // Overlay nền tối
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);

        // Vẽ tiêu đề trò chơi
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 36));
        gc.fillText("ArkanoidProject", 150, 150);

        // Vẽ danh sách các tùy chọn (Start, High Score, v.v)
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

    /**
     * Vẽ giao diện hiển thị bảng điểm cao.
     * @param gc gc.
     */
    public void renderHighScores(GraphicsContext gc) {
        Image background = AssetManager.getInstance().getImage("background");
        if (background != null) {
            gc.drawImage(background, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        // Overlay nền tối
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 32));
        gc.fillText("HIGH SCORES", 250, 100);

        // Lấy danh sách điểm cao từ HighScoreManager
        var scores = model.HighScoreManager.getInstance().getScores();
        gc.setFont(new Font("m6x11", 24));
        for (int i = 0; i < scores.size(); i++) {
            var s = scores.get(i);
            gc.fillText((i + 1) + ". " + s.getName() + " - " + s.getScore(), 220, 160 + i * 35);
        }

        // Hướng dẫn quay lại menu
        gc.setFont(new Font("m6x11", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to return", 260, 500);
    }

    /**
     * Vẽ giao diện hướng dẫn cách chơi.
     * @param gc gc.
     */
    public void renderInstruction(GraphicsContext gc) {
        // Vẽ nền hướng dẫn
        Image bg = AssetManager.getInstance().getImage("background");
        if (bg != null) {
            gc.drawImage(bg, 0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);
        }

        // Overlay nền tối
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, GameManager.SCREEN_WIDTH, GameManager.SCREEN_HEIGHT);

        // Tiêu đề hướng dẫn
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("m6x11", 40));
        gc.fillText("HOW TO PLAY", 250, 100);

        // Hiển thị các thao tác điều khiển cơ bản
        gc.setFont(new Font("m6x11", 22));
        int y = 180;
        gc.fillText("A  D : Move paddle left/right", 180, y);
        gc.fillText("SPACE : Launch the ball", 180, y + 40);
        gc.fillText("P : Pause / Resume game", 180, y + 80);
        gc.fillText("Break all bricks to win!", 180, y + 120);
        gc.fillText("Don't let the ball fall!", 180, y + 160);

        gc.setFont(new Font("m6x11", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Press ESC to return", 260, 500);
    }

    /**
     * Di chuyển con trỏ chọn lên trên trong danh sách menu.
     */
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    /**
     * Di chuyển con trỏ chọn xuống dưới trong danh sách menu.
     */
    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    /**
     * Trả về hành động tương ứng với mục đang được chọn.
     * @return Action.
     */
    public Action confirm() {
        return switch (selectedIndex) {
            case 0 -> Action.START;
            case 1 -> Action.HIGHSCORE;
            case 2 -> Action.INSTRUCTION;
            case 3 -> Action.EXIT;
            default -> Action.NONE;
        };
    }

    /**
     * Lấy chỉ số hiện tại của mục được chọn trong menu.
     * @return chỉ số.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
}
