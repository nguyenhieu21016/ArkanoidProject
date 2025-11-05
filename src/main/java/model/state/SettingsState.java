package model.state;

/**
 * SettingsState - Quản lý state của menu Settings
 * Cho phép điều chỉnh volume và các settings khác
 */
public class SettingsState {
    private int selectedIndex = 0;
    private final String[] options = {"Master Volume", "SFX Volume", "Back"};
    
    // Giá trị âm lượng (0.0 - 1.0)
    private double masterVolume = 0.7;
    private double sfxVolume = 0.7;
    
    public enum Action { NONE, BACK }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    public Action confirm() {
        return switch (selectedIndex) {
            case 2 -> Action.BACK;
            default -> Action.NONE;
        };
    }

    public void adjustVolumeLeft() {
        if (selectedIndex == 0) {
            masterVolume = Math.max(0.0, masterVolume - 0.1);
        } else if (selectedIndex == 1) {
            sfxVolume = Math.max(0.0, sfxVolume - 0.1);
        }
    }

    public void adjustVolumeRight() {
        if (selectedIndex == 0) {
            masterVolume = Math.min(1.0, masterVolume + 0.1);
        } else if (selectedIndex == 1) {
            sfxVolume = Math.min(1.0, sfxVolume + 0.1);
        }
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

    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));
    }

}



