package glous.kleebot.features.minecraft;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import glous.kleebot.References;
import glous.kleebot.features.builtin.MCWikiElement;
import glous.kleebot.http.ChromeInstance;
import glous.kleebot.utils.FileUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WikiAPI {
    private Proxy proxy;
    public WikiAPI(String proxyHost,int proxyPort){
        this.proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyHost,proxyPort));
    }
    public static final int WIKI_CN=0;
    public static final int WIKI_EN=1;
    public MCWikiElement getElement(String item , int type) throws IOException {
        String _url;
        if (type==WIKI_CN){
            _url=References.MCWIKI_CN_URL+"/"+ URLEncoder.encode(item,StandardCharsets.UTF_8);
        } else{
            _url=References.MCWIKI_EN_URL+"/"+ URLEncoder.encode(item,StandardCharsets.UTF_8);
        }
        byte[] bytes=FileUtils.download(_url,proxy);
        if (bytes==null){
            return null;
        }
        String document_content= new String(bytes,StandardCharsets.UTF_8);
        Document document= Jsoup.parse(document_content);
        Elements metaOgDescription=document.select("meta[name=description]");
        Element ogDescription=metaOgDescription.get(0);
        String description_content=ogDescription.attr("content");
        if (description_content.equals("")){
            return null;
        }
        Elements metaOgImage=document.select("meta[property=og:image]");
        Element ogImage=metaOgImage.get(0);
        Elements metaOgUrl=document.select("meta[property=og:url]");
        Element ogUrl=metaOgUrl.get(0);
        String ogImage_content=ogImage.attr("content");
        String ogImage_Base64= Base64.getEncoder().encodeToString(FileUtils.download(ogImage_content,proxy));
        String ogUrl_content=ogUrl.attr("content");
        return new MCWikiElement(description_content,ogImage_Base64,ogUrl_content);
    }
    public byte[] getElementImage(String item, int type) throws InterruptedException {
        String _url;
        if (type==WIKI_CN){
            _url=References.MCWIKI_CN_URL+"/"+ URLEncoder.encode(item,StandardCharsets.UTF_8);
        } else{
            _url=References.MCWIKI_EN_URL+"/"+ URLEncoder.encode(item,StandardCharsets.UTF_8);
        }
        byte[] image= ChromeInstance.getScreenShot(_url,0,500L);
        return image;
    }
}
