package glous.kleebot.features.builtin;

public class MCServerMOTD {

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getOnlinePlayer() {
        return onlinePlayer;
    }

    public void setOnlinePlayer(int onlinePlayer) {
        this.onlinePlayer = onlinePlayer;
    }

    private String description;
    private int protocol;
    private String name;
    private int maxPlayer;
    private int onlinePlayer;
    private int status;

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    private String favicon;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
