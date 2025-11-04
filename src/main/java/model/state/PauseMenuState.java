package model.state;

public class PauseMenuState {
    private int selectedIndex = 0;
    private final String[] options = {"Resume", "Restart", "Settings", "Main Menu", "Exit"};

    public enum Action { NONE, RESUME, RESTART, SETTINGS, MAIN_MENU, EXIT }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    public Action confirm() {
        return switch (selectedIndex) {
            case 0 -> Action.RESUME;
            case 1 -> Action.RESTART;
            case 2 -> Action.SETTINGS;
            case 3 -> Action.MAIN_MENU;
            case 4 -> Action.EXIT;
            default -> Action.NONE;
        };
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String[] getOptions() {
        return options;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.length) {
            selectedIndex = index;
        }
    }
}


