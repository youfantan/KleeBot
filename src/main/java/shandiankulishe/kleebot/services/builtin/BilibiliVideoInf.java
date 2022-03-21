package shandiankulishe.kleebot.services.builtin;

import java.util.Base64;

public class BilibiliVideoInf {
    public BilibiliVideoInf(byte[] cover, String title, String author, String url) {
        this.cover = Base64.getEncoder().encodeToString(cover);
        this.title = title;
        this.author = author;
        this.url = url;
    }

    public byte[] getCover() {
        return Base64.getDecoder().decode(cover);
    }

    public void setCover(byte[] cover) {
        this.cover = Base64.getEncoder().encodeToString(cover);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String cover;
    private String title;
    private String author;
    private String url;
}
