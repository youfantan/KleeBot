package shandiankulishe.kleebot.features.builtin;

import java.io.Serializable;

public class World implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExploration_percentage() {
        return exploration_percentage;
    }

    public void setExploration_percentage(int exploration_percentage) {
        this.exploration_percentage = exploration_percentage;
    }

    public World(String name, int exploration_percentage) {
        this.name = name;
        this.exploration_percentage = exploration_percentage;
    }

    private String name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String icon;
    private int exploration_percentage;
}
