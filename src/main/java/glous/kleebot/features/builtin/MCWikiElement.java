package glous.kleebot.features.builtin;

public class MCWikiElement {
    public MCWikiElement(String description, String image, String url) {
        this.description = description;
        this.image = image;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String description;
    private String image;
    private String url;
}
