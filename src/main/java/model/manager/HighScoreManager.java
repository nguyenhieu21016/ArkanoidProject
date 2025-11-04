package model;

import java.io.*;
import java.util.*;

public class HighScoreManager {
    private static final String FILE_PATH = "highscores.txt";
    private static final int MAX_SCORES = 10;
    private final List<ScoreEntry> scores = new ArrayList<>();

    private static HighScoreManager instance;
    public static HighScoreManager getInstance() {
        if (instance == null) instance = new HighScoreManager();
        return instance;
    }

    private HighScoreManager() {
        loadScores();
    }

    /**
     * Lưu trữ thông tin từng người chơi gồm tên và điểm số.
     */
    public static class ScoreEntry {
        private final String name;
        private final int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    /**
     * Thêm điểm mới vào danh sách và tự động sắp xếp lại.
     * @param name tên người chơi
     * @param score điểm người chơi đạt được
     */
    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        scores.sort((a, b) -> Integer.compare(b.score, a.score));
        if (scores.size() > MAX_SCORES) scores.remove(scores.size() - 1);
        saveScores();
    }

    /**
     * Trả về danh sách các điểm cao hiện tại.
     * @return danh sách ScoreEntry
     */
    public List<ScoreEntry> getScores() {
        return scores;
    }

    /**
     * Đọc dữ liệu bảng điểm từ file lưu trữ.
     */
    private void loadScores() {
        // Xóa danh sách cũ để nạp lại dữ liệu
        scores.clear();
        File file = new File(FILE_PATH);
        // Nếu file chưa tồn tại thì bỏ qua
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Tách tên và điểm từ dòng đọc được
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new ScoreEntry(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ghi danh sách điểm cao hiện tại ra file.
     */
    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Ghi từng người chơi và điểm số vào file
            for (ScoreEntry entry : scores) {
                writer.write(entry.name + ";" + entry.score);
                // Xuống dòng cho mỗi bản ghi
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
