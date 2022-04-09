package glous.kleebot.features.pixiv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import glous.kleebot.References;
import glous.kleebot.async.Timer;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.utils.FileUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PixivAPI {
    private Proxy proxy;
    public PixivAPI(String proxyHost,int proxyPort){
        this.proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyHost,proxyPort));
    }
    public byte[] getImage(String url) throws IOException {
        HashMap<String,String> headers=new HashMap<>();
        headers.put("referer","http://www.pixiv.net");
        headers.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        byte[] cache;
        if ((cache=CacheFactory.getCache(url))!=null){
            return cache;
        } else {
            byte[] content=FileUtils.download(url,proxy,headers);
            CacheFactory.storeCache(url,content, Timer.NO_LIMIT);
            return content;
        }
    }
    public HashMap<String,String> getArtwork(int illustid) {
        HashMap<String, String> artwork = new LinkedHashMap<>();
        String url = References.PIXIV_ILLUST_API.formatted(illustid);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("referer", "http://www.pixiv.net");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        String retApi;
        byte[] cache;
        if ((cache = CacheFactory.getCache("illust:" + illustid)) != null) {
            retApi = new String(cache, StandardCharsets.UTF_8);
        } else {
            byte[] api=FileUtils.download(url, proxy, headers);
            if (api==null){
                return null;
            }
            retApi=new String(api,StandardCharsets.UTF_8);
            CacheFactory.storeCache("illust:"+illustid,retApi.getBytes(StandardCharsets.UTF_8),Timer.NO_LIMIT);
        }
        JsonObject object=JsonParser.parseString(retApi).getAsJsonObject();
        JsonObject body=object.get("body").getAsJsonObject();
        artwork.put("title",body.get("illustTitle").getAsString());
        artwork.put("author",body.get("userName").getAsString());
        if (body.get("xRestrict").getAsInt()==0){
            artwork.put("sexual","false");
        } else{
            artwork.put("sexual","true");
        }
        artwork.put("imageUrl",body.get("urls").getAsJsonObject().get("original").getAsString());
        artwork.put("date",body.get("uploadDate").getAsString());
        return artwork;
    }
    public HashMap<Integer,HashMap<String,String>> getDailyRanking() throws IOException {
        return getRanking(References.PIXIV_RANKING_DAILY);
    }
    public HashMap<Integer,HashMap<String,String>> getWeeklyRanking() throws IOException {
        return getRanking(References.PIXIV_RANKING_WEEKLY);
    }
    public HashMap<Integer,HashMap<String,String>> getMonthlyRanking() throws IOException {
        return getRanking(References.PIXIV_RANKING_MONTHLY);
    }
    public HashMap<Integer,HashMap<String,String>> getRanking(String url) throws IOException {
        HashMap<Integer,HashMap<String,String>> result=new HashMap<>();
        String retApi;
        byte[] cacheContent;
        if ((cacheContent=CacheFactory.getCache(url))!=null){
            retApi=new String(cacheContent,StandardCharsets.UTF_8);
        } else{
            retApi= new String(FileUtils.download(url,proxy), StandardCharsets.UTF_8);
            CacheFactory.storeCache(url,retApi.getBytes(StandardCharsets.UTF_8), Timer.HOUR*2);
        }
        JsonObject object=JsonParser.parseString(retApi).getAsJsonObject();
        JsonArray array=object.get("contents").getAsJsonArray();
        for (int i=0;i<array.size();i++){
            JsonObject content=array.get(i).getAsJsonObject();
            HashMap<String,String> artwork=getArtwork(Integer.parseInt(content.get("illust_id").getAsString()));
            result.put(i,artwork);
        }
        return result;
    }
}
