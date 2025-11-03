package model;

public class MenuState {
    private int selectedIndex = 0;
    private final String[] options = {"Start", "High Score", "Instruction", "Exit"};

    public enum Action { NONE, START, HIGHSCORE, INSTRUCTION, EXIT }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    public Action confirm() {
        return switch (selectedIndex) {
            case 0 -> Action.START;
            case 1 -> Action.HIGHSCORE;
            case 2 -> Action.INSTRUCTION;
            case 3 -> Action.EXIT;
            default -> Action.NONE;
        };
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String[] getOptions() {
        return options;
    }
}

