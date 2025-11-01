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

    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        scores.sort((a, b) -> Integer.compare(b.score, a.score));
        if (scores.size() > MAX_SCORES) scores.remove(scores.size() - 1);
        saveScores();
    }

    public List<ScoreEntry> getScores() {
        return scores;
    }

    private void loadScores() {
        scores.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
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

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (ScoreEntry entry : scores) {
                writer.write(entry.name + ";" + entry.score);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
