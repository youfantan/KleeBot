package glous.kleebot.features.bilibili;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import glous.kleebot.services.builtin.BilibiliVideoInf;
import glous.kleebot.References;
import glous.kleebot.async.Timer;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.utils.FileUtils;
import glous.kleebot.utils.HttpUtils;
import glous.kleebot.utils.RandomUtils;
import glous.kleebot.utils.builtin.HttpResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BilibiliAPI {
    public String getShortUrl(String originUrl) throws IOException {
        StringWriter writer=new StringWriter();
        JsonWriter json=new JsonWriter(writer);
        json.beginObject();
        json.name("build").value(6500300);
        json.name("buvid").value(RandomUtils.newRandomMixedString(32)+"infoc");
        json.name("oid").value(originUrl);
        json.name("platform").value("android");
        json.name("share_channel").value("COPY");
        json.name("share_id").value("public.webview.0.0.pv");
        json.name("share_mode").value(3);
        json.endObject();
        json.close();
        writer.close();
        HttpUtils httpUtils=new HttpUtils();
        HttpResponse resp=httpUtils.post(References.BILIBILI_SHORT_URL,null,Map.of("Content-Type","application/json"),writer.toString().getBytes(StandardCharsets.UTF_8));
        if (resp.getResponseCode()!=200){
            return "error return value: "+resp.getResponseCode();
        }
        JsonObject object= JsonParser.parseString(new String(resp.getBody(),StandardCharsets.UTF_8)).getAsJsonObject();
        if (!object.has("data")){
            return object.get("message").getAsString();
        }
        JsonObject data=object.get("data").getAsJsonObject();
        if (!data.has("content")){
            return "not a url like [bilibili/hdslb]";
        }
        return data.get("content").getAsString();
    }
    public String getBVid(String rawUrl){
        if (rawUrl.length()<rawUrl.indexOf("BV")+12){
            return null;
        }
        return rawUrl.substring(rawUrl.indexOf("BV"),rawUrl.indexOf("BV")+12);
    }
    public String getCid(String BVid) throws IOException {
        String playerList= new String(FileUtils.download(References.BILBILI_PLAYER_LIST_URL.formatted(BVid)), StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(playerList).getAsJsonObject();
        return String.valueOf(object.getAsJsonArray("data").get(0).getAsJsonObject().get("cid").getAsLong());
    }
    public String getAid(String Cid,String BVid) throws IOException {
        String webInterface=new String(FileUtils.download(References.BILBILI_WEB_INTERFACE_BVID_URL.formatted(Cid,BVid)),StandardCharsets.UTF_8);
        JsonObject object=JsonParser.parseString(webInterface).getAsJsonObject();
        return String.valueOf(object.getAsJsonObject("data").get("aid").getAsLong());
    }
    public BilibiliVideoInf getVideoInformation(String Aid) throws IOException {
        byte[] formattedInf;
        Gson gson=new Gson();
        if ((formattedInf= CacheFactory.getCache(Aid))!=null){
            BilibiliVideoInf inf=gson.fromJson(new String(formattedInf,StandardCharsets.UTF_8),BilibiliVideoInf.class);
            return inf;
        } else {
            String webInterface=new String(FileUtils.download(References.BILBILI_WEB_INTERFACE_AID_URL.formatted(Aid)),StandardCharsets.UTF_8);
            JsonObject object=JsonParser.parseString(webInterface).getAsJsonObject();
            JsonObject data=object.getAsJsonObject("data");
            String coverUrl=data.get("pic").getAsString();
            byte[] cover=FileUtils.download(coverUrl);
            String title=data.get("title").getAsString();
            JsonObject owner=data.get("owner").getAsJsonObject();
            String author=owner.get("name").getAsString();
            String url="https://www.bilibili.com/video/"+data.get("bvid").getAsString();
            BilibiliVideoInf inf=new BilibiliVideoInf(cover,title,author,url);
            formattedInf=gson.toJson(inf).getBytes(StandardCharsets.UTF_8);
            CacheFactory.storeCache(Aid,formattedInf, Timer.NO_LIMIT);
            return inf;
        }
    }
    public String getB23RedirectUrl(String b23Url) throws IOException {
        URL url=new URL(b23Url);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        if (connection.getResponseCode()!=302){
            return "error status code: "+connection.getResponseCode();
        }
        return connection.getHeaderField("Location");
    }
}
