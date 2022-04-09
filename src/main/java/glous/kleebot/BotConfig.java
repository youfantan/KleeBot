package glous.kleebot;


public class BotConfig {
    public String getProxyHost() {
        return ProxyHost;
    }

    public void setProxyHost(String proxyHost) {
        ProxyHost = proxyHost;
    }

    public int getProxyPort() {
        return ProxyPort;
    }

    public void setProxyPort(int proxyPort) {
        ProxyPort = proxyPort;
    }

    public int getQueueSize() {
        return QueueSize;
    }

    public void setQueueSize(int queueSize) {
        QueueSize = queueSize;
    }

    public long getBotAccount() {
        return BotAccount;
    }

    public void setBotAccount(long botAccount) {
        BotAccount = botAccount;
    }

    public String getBotPassword() {
        return BotPassword;
    }

    public void setBotPassword(String botPassword) {
        BotPassword = botPassword;
    }

    public String getCacheDir() {
        return CacheDir;
    }

    public void setCacheDir(String cacheDir) {
        CacheDir = cacheDir;
    }

    public int getServicePort() {
        return ServicePort;
    }

    public void setServicePort(int servicePort) {
        ServicePort = servicePort;
    }

    public String getCookieFile() {
        return CookieFile;
    }

    public void setCookieFile(String cookieFile) {
        CookieFile = cookieFile;
    }

    public String getResourcePackDir() {
        return ResourcePackDir;
    }

    public void setResourcePackDir(String resourcePackDir) {
        ResourcePackDir = resourcePackDir;
    }

    public String getResourcePackFileDir() {
        return ResourcePackFileDir;
    }

    public void setResourcePackFileDir(String resourcePackFileDir) {
        ResourcePackFileDir = resourcePackFileDir;
    }

    private String ProxyHost;
    private int ProxyPort;
    private int QueueSize;
    private long BotAccount;
    private String BotPassword;
    private String CacheDir;
    private int ServicePort;
    private String CookieFile;
    private String ResourcePackDir;
    private String ResourcePackFileDir;
    private boolean SilentMode;

    public boolean isSilentMode() {
        return SilentMode;
    }

    public void setSilentMode(boolean silentMode) {
        SilentMode = silentMode;
    }
}
