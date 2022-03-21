package shandiankulishe.kleebot;

public class BotConfig {
    public long[] getEnableGroups() {
        return EnableGroups;
    }

    public int getQueueSize() {
        return QueueSize;
    }

    public long getBotAccount() {
        return BotAccount;
    }

    public String getBotPassword() {
        return BotPassword;
    }

    public String getProxyHost() {
        return ProxyHost;
    }

    public void setProxyHost(String proxyHost) {
        ProxyHost = proxyHost;
    }

    public int getProxyPort() {
        return ProxyPort;
    }

    private String ProxyHost;
    private int ProxyPort;
    private long[] EnableGroups;
    private int QueueSize;
    private long BotAccount;
    private String BotPassword;

    public String getCacheDir() {
        return CacheDir;
    }

    public void setCacheDir(String cacheDir) {
        CacheDir = cacheDir;
    }

    private String CacheDir;
    public String getResourcePackFileDir() {
        return ResourcePackFileDir;
    }

    public void setResourcePackFileDir(String resourcePackFileDir) {
        ResourcePackFileDir = resourcePackFileDir;
    }

    private String ResourcePackFileDir;

    public String getResourcePackDir() {
        return ResourcePackDir;
    }

    public void setResourcePackDir(String resourcePackDir) {
        ResourcePackDir = resourcePackDir;
    }

    private String ResourcePackDir;

    public String getCookieFile() {
        return CookieFile;
    }

    public void setCookieFile(String cookieFile) {
        CookieFile = cookieFile;
    }

    private String CookieFile;

    public int getServicePort() {
        return ServicePort;
    }

    public void setServicePort(int servicePort) {
        ServicePort = servicePort;
    }

    private int ServicePort;
}
