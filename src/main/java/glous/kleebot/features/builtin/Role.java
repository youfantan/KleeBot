package glous.kleebot.features.builtin;

import java.io.Serializable;

public class Role implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAvatar_image() {
        return avatar_image;
    }

    public void setAvatar_image(String avatar_image) {
        this.avatar_image=avatar_image;
    }

    public int getFetter() {
        return fetter;
    }

    public void setFetter(int fetter) {
        this.fetter = fetter;
    }

    public Role(String name, int level, String avatar_image, int fetter) {
        this.name = name;
        this.level = level;
        this.avatar_image = avatar_image;
        this.fetter = fetter;
    }

    private String name;
    private int level;
    private String avatar_image;
    private int fetter;
}
