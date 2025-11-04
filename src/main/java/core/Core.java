package core;

import model.manager.GameManager;
import util.AssetManager;
import view.GameView;
import controller.InputController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Core extends Application {

    /**
     * Khởi tạo và thiết lập window.
     * @param primaryStage primaryStage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Model - quản lí logic game (Singleton)
        GameManager gameManager = GameManager.getInstance();

        // View - xử lí giao diện
        GameView gameView = new GameView(gameManager);

        // Controller - xử lí điều khiển
        InputController inputController = new InputController(gameManager);

        // Tải tài nguyên game
        AssetManager.getInstance().loadAssets();
        
        // Tải âm thanh (delay để tránh lỗi module access khi chạy từ IDE)
        Platform.runLater(() -> {
            try {
                util.SoundManager.getInstance().loadSounds();
            } catch (Exception e) {
                System.err.println("Không thể tải âm thanh (có thể thiếu VM options): " + e.getMessage());
                System.err.println("Hãy thêm các VM options sau vào Run Configuration:");
                System.err.println("--add-modules javafx.controls,javafx.fxml,javafx.media");
                System.err.println("--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED");
                System.err.println("--add-opens javafx.base/com.sun.javafx=ALL-UNNAMED");
            }
        });

        // Khởi tạo Scene và gắn InputController để nghe event
        Scene scene = new Scene(gameView.getRoot());
        inputController.listenTo(scene);

        // Thiết lập các thuộc tính cho window
        primaryStage.setTitle("ArkanoidProject");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Bắt đầu vòng lặp chính của game
        gameView.startGameLoop();
    }

    /**
     * Hàm main.
     * @param args agrs.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
