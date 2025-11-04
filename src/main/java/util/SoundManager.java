package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager - Quản lý hệ thống âm thanh cho game
 * Chỉ cần đặt file âm thanh vào thư mục resources/sounds/ và gọi playSound() với tên file
 */
public class SoundManager {

    // Singleton instance
    private static SoundManager instance;

    // Map để lưu trữ các Media đã được tải
    private final Map<String, Media> sounds = new HashMap<>();

    // Volume mặc định (0.0 - 1.0)
    private double masterVolume = 0.7;

    // Constructor private để ngăn việc tạo instance từ bên ngoài
    private SoundManager() {}

    /**
     * Lấy instance duy nhất của SoundManager
     * @return SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Tải tất cả các file âm thanh từ thư mục resources/sounds/
     * Tự động phát hiện các file .mp3, .wav, .m4a trong thư mục
     * Hỗ trợ cả khi chạy từ IDE và JAR file
     */
    public void loadSounds() {
        try {
            // Lấy URL của thư mục sounds
            URL soundsDir = getClass().getResource("/sounds/");
            if (soundsDir == null) {
                System.err.println("Không tìm thấy thư mục /sounds/ trong resources");
                return;
            }

            // Nếu là file: protocol (khi chạy từ IDE)
            if (soundsDir.getProtocol().equals("file")) {
                File dir = new File(soundsDir.toURI());
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles((d, name) -> 
                        name.toLowerCase().endsWith(".mp3") || 
                        name.toLowerCase().endsWith(".wav") ||
                        name.toLowerCase().endsWith(".m4a")
                    );
                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName();
                            String soundName = fileName.substring(0, fileName.lastIndexOf('.'));
                            loadSound(soundName, "/sounds/" + fileName);
                        }
                    }
                }
            } else {
                // Nếu là jar: protocol (khi chạy từ JAR)
                // Thử tải các file âm thanh phổ biến
                // Bạn có thể thêm file mới vào đây hoặc gọi loadSound() trực tiếp
                String[] commonSounds = {"brick_hit"};
                String[] extensions = {".mp3", ".wav", ".m4a"};
                
                for (String soundName : commonSounds) {
                    boolean loaded = false;
                    for (String ext : extensions) {
                        if (getClass().getResource("/sounds/" + soundName + ext) != null) {
                            loadSound(soundName, "/sounds/" + soundName + ext);
                            loaded = true;
                            break;
                        }
                    }
                    if (!loaded) {
                        System.out.println("Không tìm thấy file âm thanh: " + soundName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải một file âm thanh cụ thể
     * @param soundName Tên định danh (không có extension)
     * @param path Đường dẫn trong resources (ví dụ: "/sounds/brick_hit.mp3")
     */
    public void loadSound(String soundName, String path) {
        try {
            URL soundUrl = getClass().getResource(path);
            if (soundUrl == null) {
                System.err.println("Không tìm thấy file âm thanh: " + path);
                return;
            }
            Media media = new Media(soundUrl.toString());
            sounds.put(soundName, media);
        } catch (IllegalAccessError e) {
            System.err.println("Lỗi module access khi tải âm thanh " + path);
            System.err.println("Hãy thêm các VM options sau vào Run Configuration:");
            System.err.println("--add-modules javafx.controls,javafx.fxml,javafx.media");
            System.err.println("--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED");
            System.err.println("--add-opens javafx.base/com.sun.javafx=ALL-UNNAMED");
        } catch (Exception e) {
            System.err.println("Không thể tải âm thanh " + path + ": " + e.getMessage());
            if (e.getCause() instanceof IllegalAccessError) {
                System.err.println("Lỗi: Thiếu VM options. Hãy xem file VM_OPTIONS.txt để biết cách cấu hình.");
            }
        }
    }

    /**
     * Phát một âm thanh đã được tải
     * @param soundName Tên định danh của âm thanh (không có extension)
     */
    public void playSound(String soundName) {
        playSound(soundName, masterVolume);
    }

    /**
     * Phát một âm thanh với volume tùy chỉnh
     * @param soundName Tên định danh của âm thanh
     * @param volume Volume (0.0 - 1.0)
     */
    public void playSound(String soundName, double volume) {
        Media media = sounds.get(soundName);
        if (media == null) {
            // Thử tải tự động nếu chưa được tải
            loadSound(soundName, "/sounds/" + soundName + ".mp3");
            media = sounds.get(soundName);
            if (media == null) {
                System.err.println("Âm thanh không tồn tại: " + soundName);
                return;
            }
        }

        try {
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Lỗi khi phát âm thanh " + soundName + ": " + e.getMessage());
        }
    }

    /**
     * Phát một âm thanh và lặp lại
     * @param soundName Tên định danh của âm thanh
     * @return MediaPlayer để có thể dừng sau này
     */
    public MediaPlayer playSoundLooping(String soundName) {
        return playSoundLooping(soundName, masterVolume);
    }

    /**
     * Phát một âm thanh và lặp lại với volume tùy chỉnh
     * @param soundName Tên định danh của âm thanh
     * @param volume Volume (0.0 - 1.0)
     * @return MediaPlayer để có thể dừng sau này
     */
    public MediaPlayer playSoundLooping(String soundName, double volume) {
        Media media = sounds.get(soundName);
        if (media == null) {
            loadSound(soundName, "/sounds/" + soundName + ".mp3");
            media = sounds.get(soundName);
            if (media == null) {
                System.err.println("Âm thanh không tồn tại: " + soundName);
                return null;
            }
        }

        try {
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
            return mediaPlayer;
        } catch (Exception e) {
            System.err.println("Lỗi khi phát âm thanh lặp " + soundName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Đặt volume tổng thể
     * @param volume Volume (0.0 - 1.0)
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
    }

    /**
     * Lấy volume tổng thể hiện tại
     * @return Volume (0.0 - 1.0)
     */
    public double getMasterVolume() {
        return masterVolume;
    }

    /**
     * Kiểm tra xem một âm thanh đã được tải chưa
     * @param soundName Tên định danh của âm thanh
     * @return true nếu đã được tải
     */
    public boolean isSoundLoaded(String soundName) {
        return sounds.containsKey(soundName);
    }
}

