package shandiankulishe.kleebot.cache;

import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;

public class CacheFactory {
    public static byte[] long2byte(long res) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((res >> offset) & 0xff);
        }
        return buffer;
    }
    public static String getCacheName(){
        CRC32 code=new CRC32();
        code.reset();
        long cTime=System.currentTimeMillis();
        byte[] time=long2byte(cTime);
        code.update(time);
        return Long.toHexString(code.getValue());
    }
    private static HashMap<String ,String> cacheMap=new HashMap<>();
    public static void storeCache(String name, byte[] content, long expired){
        String cachePath;
        Cache cache;
        if ((cachePath=cacheMap.get(name))!=null){
            cache=Cache.restore(cachePath);
            try {
                Field fContent=cache.getClass().getDeclaredField("content");
                Field fExpired=cache.getClass().getDeclaredField("expired");
                fContent.setAccessible(true);
                fExpired.setAccessible(true);
                fContent.set(cache,content);
                fExpired.set(cache,expired);
                cache.store();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else{
            cache=new Cache(content,expired);
            cache.store();
            cacheMap.put(name,cache.getCachePath());
        }
    }
    public static byte[] getCache(String cacheName){ ;
        if (cacheMap.containsKey(cacheName)){
            Cache cache=Cache.restore(cacheMap.get(cacheName));
            long saved=System.currentTimeMillis()-cache.getSaveTime();
            if (cache.getExpired()==-1||saved<cache.getExpired()){
                return cache.getContent();
            }
        }
        return null;
    }
    public static void serializeCaches() throws IOException {
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        for (Map.Entry<String,String> s:
             cacheMap.entrySet()) {
            bout.write(s.getKey().getBytes(StandardCharsets.UTF_8));
            bout.write(0x01);
            bout.write(s.getValue().getBytes(StandardCharsets.UTF_8));
            bout.write(0x02);
        }
        bout.flush();
        bout.close();
        FileUtils.writeFile(KleeBot.config.getCacheDir()+File.separatorChar+"cache.map",bout.toByteArray());
    }
    public static void deserializeCaches() throws IOException {
        if (new File(KleeBot.config.getCacheDir()+File.separatorChar+"cache.map").exists()){
            ByteArrayInputStream bin=new ByteArrayInputStream(Objects.requireNonNull(FileUtils.readFile(KleeBot.config.getCacheDir() + File.separatorChar + "cache.map")));
            byte[] total=bin.readAllBytes();
            ByteArrayOutputStream key=new ByteArrayOutputStream();
            ByteArrayOutputStream value=new ByteArrayOutputStream();
            boolean status=true;
            for (byte b :
                    total) {
                if (status){
                    if (b==0x01){
                        status=false;
                    } else{
                        key.write(b);
                    }
                } else{
                    if (b!=0x02){
                        value.write(b);
                    } else{
                        String sKey= key.toString(StandardCharsets.UTF_8);
                        String sValue=value.toString(StandardCharsets.UTF_8);
                        cacheMap.put(sKey,sValue);
                        key.close();
                        value.close();
                        key=new ByteArrayOutputStream();
                        value=new ByteArrayOutputStream();
                        status=true;
                    }
                }
            }
            key.close();
            value.close();
        }
    }
}
