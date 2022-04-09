package glous.kleebot.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import glous.kleebot.KleeBot;
import glous.kleebot.async.Task;
import glous.kleebot.async.Timer;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.services.GroupService;
import glous.kleebot.utils.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoPostService extends GroupService {
    private static HashMap<Long,List<String>> enableList=new HashMap<>();
    private static final List<String> availableFeatures=List.of("mcv");
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" enable ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        logger.trace("test");
        String message=event.getMessage().serializeToMiraiCode();
        String feature=message.substring(message.indexOf("enable")+7);
        System.out.println(feature);
        List<String> features;
        enableList.computeIfAbsent(event.getGroup().getId(), k -> new ArrayList<String>());
        features=enableList.get(event.getGroup().getId());
        if (availableFeatures.contains(feature)) {
            if (features.contains(feature)) {
                sendMessage("%s 功能重复启用".formatted(feature),event);
            } else{
                features.add(feature);
                enableList.put(event.getGroup().getId(),features);
                sendMessage("已成功启用 %s 功能".formatted(feature),event);
            }
            return true;
        } else{
            return false;
        }
    }

    @Override
    public String getServiceName() {
        return super.getServiceName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize() {
        byte[] bytes;
        if ((bytes=CacheFactory.getCache("AutoPostServiceConfig"))!=null){
            ByteArrayInputStream in=new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream ois=new ObjectInputStream(in);
                enableList=(HashMap<Long, List<String>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Timer.registerScheduledTask(new Task(()->{
            //update version
            try {
                byte[] manifest_bytes=FileUtils.download("https://launchermeta.mojang.com/mc/game/version_manifest.json");
                String manifest=new String(manifest_bytes, StandardCharsets.UTF_8);
                JsonElement ele=JsonParser.parseString(manifest);
                JsonObject object=ele.getAsJsonObject();
                JsonObject latest=object.get("latest").getAsJsonObject();
                String latestRelease=latest.get("release").getAsString();
                String latestSnapshot=latest.get("snapshot").getAsString();
                byte[] bytes1;
                byte[] bytes2;
                boolean haveHigherVersion=false;
                if ((bytes1=CacheFactory.getCache("mcv_latest_release"))!=null){
                    String behindRelease=new String(bytes1);
                    if (!behindRelease.equals(latestRelease)){
                        haveHigherVersion=true;
                    }
                }
                if ((bytes2=CacheFactory.getCache("mcv_latest_snapshot"))!=null){
                    String behindSnapshot=new String(bytes2);
                    if (!behindSnapshot.equals(latestSnapshot)){
                        haveHigherVersion=true;
                    }
                }
                String snapshotDetails="RELEASE_DETAILS";
                String releaseDetails="SNAPSHOT_DETAILS";
                if (haveHigherVersion){
                    JsonArray versions=object.get("versions").getAsJsonArray();
                    for (int i = 0; i < versions.size(); i++) {
                        JsonObject obj=versions.get(i).getAsJsonObject();
                        if (obj.get("id").getAsString().equals(latestSnapshot)){
                            snapshotDetails="发布时间: %s 类型: %s 清单文件URL: %s".formatted(obj.get("releaseTime").getAsString(),obj.get("type").getAsString(),obj.get("url").getAsString());
                        }
                        if (obj.get("id").getAsString().equals(latestRelease)){
                            releaseDetails="发布时间: %s 类型: %s 清单文件URL: %s".formatted(obj.get("releaseTime").getAsString(),obj.get("type").getAsString(),obj.get("url").getAsString());
                        }
                    }
                }
                CacheFactory.storeCache("mcv_latest_snapshot",latestSnapshot.getBytes(StandardCharsets.UTF_8),Timer.NO_LIMIT);
                CacheFactory.storeCache("mcv_latest_release",latestRelease.getBytes(StandardCharsets.UTF_8),Timer.NO_LIMIT);
                for (long key :
                        enableList.keySet()) {
                    if (enableList.get(key).contains("mcv")){
                        if (haveHigherVersion){
                            KleeBot.botInstance.getGroup(key).sendMessage("检测到Minecraft新版本: \n正式版: %s(%s)\n快照版: %s(%s)".formatted(latestRelease,releaseDetails,latestSnapshot,snapshotDetails));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        },this.getClass().getName()+"#MCV"),Timer.MINUTE*10);
        logger.info(getServiceName()+" initialized successfully");
    }

    @Override
    public void stop() {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(out);
            oos.writeObject(enableList);
            oos.flush();
            oos.close();
            out.close();
            CacheFactory.storeCache("AutoPostServiceConfig",out.toByteArray(), Timer.NO_LIMIT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
