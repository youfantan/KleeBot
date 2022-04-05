package shandiankulishe.kleebot.features.builtin;

import java.io.Serializable;

public class AbyssFloor implements Serializable {
    public AbyssFloor(int index, int star, int max_star) {
        this.index = index;
        this.star = star;
        this.max_star = max_star;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getMax_star() {
        return max_star;
    }

    public void setMax_star(int max_star) {
        this.max_star = max_star;
    }

    private int index;
    private int star;
    private int max_star;
}
