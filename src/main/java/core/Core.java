package core;

import model.GameManager;
import view.TerminalView;

import java.awt.*;

public class Core {
    public static void main(String[] args) {
        System.out.println("Khởi tạo Arkanoid...");

        try {

            // Khởi tạo Model
            GameManager gameManager = new GameManager();

            // Khởi tạo View
            TerminalView view = new TerminalView(80, 24);

            // Khởi tạo Controller

            System.out.println("BẮT ĐẦU!");

            while (!gameManager.isGameOver() && !gameManager.isGameWon()) {
                // Controller


                // Model
                gameManager.updateGame();

                // View
                view.render(gameManager);

                // Tạm dừng một chút để gâme không chạy quá nhanh và giảm tải CPU
                Thread.sleep(50); // 20FPS (1000ms / 50ms)
            }

            // Kết thúc
            view.render(gameManager);
            System.out.println("KẾT THÚC!");
        } catch (InterruptedException e) {
            // Xử lý nếu luồn game bị gián đoạn đột ngột
            Thread.currentThread().interrupt();
            System.err.println("Luồng game đã bị gián đoạn!");

        }

    }
}
