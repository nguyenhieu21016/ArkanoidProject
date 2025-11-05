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

    // Singleton duy nhất
    private static SoundManager instance;

    private static final String[] SUPPORTED_EXTENSIONS = {".mp3", ".wav", ".m4a"};
    private static final String[] COMMON_SOUND_HINTS = {"brickHit", "paddleHit", "powerUp", "optionChange", "selected", "mainMenuBGM", "inGameBGM", "gameOverBGM", "brick_hit"};

    // Map lưu các Media (âm thanh) đã tải
    private final Map<String, Media> sounds = new HashMap<>();
    private final Map<String, String> aliasMap = new HashMap<>();

    // Âm lượng mặc định (0.0 - 1.0)
    private double masterVolume = 0.7;
    private double sfxVolume = 0.7;

    // Constructor private để ngăn tạo instance từ bên ngoài
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
            // Lấy URL thư mục sounds
            URL soundsDir = getClass().getResource("/sounds/");
            if (soundsDir == null) {
                System.err.println("Không tìm thấy thư mục /sounds/ trong resources");
                return;
            }

            // Nếu là giao thức file: (chạy từ IDE)
            if (soundsDir.getProtocol().equals("file")) {
                File dir = new File(soundsDir.toURI());
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles((d, name) -> {
                        String lower = name.toLowerCase();
                        for (String ext : SUPPORTED_EXTENSIONS) {
                            if (lower.endsWith(ext)) {
                                return true;
                            }
                        }
                        return false;
                    });
                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName();
                            String soundName = fileName.substring(0, fileName.lastIndexOf('.'));
                            loadSound(soundName, "/sounds/" + fileName);
                        }
                    }
                }
            } else {
                // Nếu là giao thức jar: (chạy từ JAR)
                // Thử tải các file âm thanh phổ biến
                // Có thể thêm file mới tại đây hoặc gọi loadSound() trực tiếp
                for (String soundName : COMMON_SOUND_HINTS) {
                    boolean loaded = false;
                    for (String ext : SUPPORTED_EXTENSIONS) {
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
            aliasMap.put(normalizeKey(soundName), soundName);
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
        double resolvedVolume = clampVolume(masterVolume * sfxVolume);
        playSoundInternal(soundName, resolvedVolume);
    }

    /**
     * Phát một âm thanh với volume tùy chỉnh
     * @param soundName Tên định danh của âm thanh
     * @param volume Volume (0.0 - 1.0)
     */
    public void playSound(String soundName, double volume) {
        double resolvedVolume = clampVolume(masterVolume * sfxVolume * volume);
        playSoundInternal(soundName, resolvedVolume);
    }

    private void playSoundInternal(String soundName, double volume) {
        String key = resolveSoundName(soundName);
        if (key == null) {
            System.err.println("Âm thanh không tồn tại: " + soundName);
            return;
        }

        Media media = sounds.get(key);
        if (media == null) {
            System.err.println("Âm thanh không tồn tại: " + soundName);
            return;
        }

        try {
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(clampVolume(volume));
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
        String key = resolveSoundName(soundName);
        if (key == null) {
            System.err.println("Âm thanh không tồn tại: " + soundName);
            return null;
        }

        Media media = sounds.get(key);
        if (media == null) {
            System.err.println("Âm thanh không tồn tại: " + soundName);
            return null;
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

    public void stopLoop(MediaPlayer player) {
        if (player == null) return;
        try {
            player.stop();
            player.dispose();
        } catch (Exception e) {
            System.err.println("Không thể dừng âm thanh: " + e.getMessage());
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

    public void setSfxVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    /**
     * Kiểm tra xem một âm thanh đã được tải chưa
     * @param soundName Tên định danh của âm thanh
     * @return true nếu đã được tải
     */
    public boolean isSoundLoaded(String soundName) {
        return sounds.containsKey(soundName);
    }

    private String resolveSoundName(String requested) {
        if (requested == null || requested.isEmpty()) {
            return null;
        }

        if (sounds.containsKey(requested)) {
            return requested;
        }

        String normalized = normalizeKey(requested);
        String alias = aliasMap.get(normalized);
        if (alias != null && sounds.containsKey(alias)) {
            return alias;
        }

        if (attemptAutoLoad(requested) && sounds.containsKey(requested)) {
            return requested;
        }

        if (alias != null && attemptAutoLoad(alias) && sounds.containsKey(alias)) {
            return alias;
        }

        for (String hint : COMMON_SOUND_HINTS) {
            if (normalizeKey(hint).equals(normalized) && attemptAutoLoad(hint) && sounds.containsKey(hint)) {
                return hint;
            }
        }

        return null;
    }

    private boolean attemptAutoLoad(String soundName) {
        if (soundName == null || soundName.isEmpty() || sounds.containsKey(soundName)) {
            return sounds.containsKey(soundName);
        }

        for (String ext : SUPPORTED_EXTENSIONS) {
            String path = "/sounds/" + soundName + ext;
            if (getClass().getResource(path) != null) {
                loadSound(soundName, path);
                return sounds.containsKey(soundName);
            }
        }
        return false;
    }

    private String normalizeKey(String name) {
        return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private double clampVolume(double volume) {
        return Math.max(0.0, Math.min(1.0, volume));
    }
}

