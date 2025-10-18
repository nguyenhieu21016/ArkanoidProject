package util;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {

    // Biến static để giữ instance duy nhất
    private static AssetManager instance;

    // Map để lưu trữ các hình ảnh đã được tải
    private final Map<String, Image> images = new HashMap<>();

    // Constructor là private để ngăn việc tạo instance từ bên ngoài
    private AssetManager() {}

    /**
     * Public, static để lấy instance duy nhất.
     * @return instance
     */
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Tải tất cả các tài nguyên cần thiết cho game.
     */
    public void loadAssets() {
        loadImage("background", "/images/background.png");
        loadImage("paddle", "/images/paddle.png");
    }

    /**
     * Tải một ảnh và đưa vào map.
     * @param key tên định danh
     * @param path đường dẫn
     */
    private void loadImage(String key, String path) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            images.put(key, image);
        } catch (Exception e) {
            System.err.println("Không thể tải tài nguyên ảnh" + path);
        }
    }

    /**
     * Lấy một ảnh đã được tải từ kho tài nguyên.
     * @param key tên định danh
     * @return image
     */
    public Image getImage(String key) {
        return images.get(key);
    }
}
