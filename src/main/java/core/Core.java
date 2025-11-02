package core;

import model.GameManager;
import util.AssetManager;
import view.GameView;
import controller.InputController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Core extends Application {

    /**
     * Khởi tạo và thiết lập window.
     * @param primaryStage primaryStage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Model - quản lí logic game
        GameManager gameManager = new GameManager();

        // View - xử lí giao diện
        GameView gameView = new GameView(gameManager);

        // Controller - xử lí điều khiển
        InputController inputController = new InputController(gameManager);

        // Tải tài nguyên game
        AssetManager.getInstance().loadAssets();

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
