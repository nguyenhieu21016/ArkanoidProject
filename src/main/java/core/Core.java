package core;

import model.GameManager;
import util.AssetManager;
import view.GameView;
import controller.InputController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Core extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Model
        GameManager gameManager = new GameManager();

        // View
        GameView gameView = new GameView(gameManager);

        // Controller
        InputController inputController = new InputController(gameManager);

        // Asset
        AssetManager.getInstance().loadAssets();

        // Tạo Scene
        Scene scene = new Scene(gameView.getRoot());
        inputController.listenTo(scene);

        primaryStage.setTitle("ArkanoidProject");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gameView.startGameLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
