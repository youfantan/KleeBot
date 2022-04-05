package shandiankulishe.kleebot.features.github;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class GithubAPI {
    private Proxy proxy;
    private String authKey;
    public GithubAPI(String proxyHost,int proxyPort, String authKey){
        this.proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyHost,proxyPort));
        this.authKey=authKey;
    }
    public void getUser(String usr){
    }
}
