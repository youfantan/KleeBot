package shandiankulishe.kleebot.cache;

import shandiankulishe.kleebot.KleeBot;
import java.io.*;
import java.util.UUID;

public class Cache implements Serializable {
    private byte[] content;
    private long expired;
    private long saveTime;
    private String cacheName;
    private String cachePath;
    public Cache(byte[] content,long expired){
        this.content=content;
        this.expired=expired;
        this.cacheName=UUID.randomUUID().toString();
        this.saveTime=System.currentTimeMillis();
        this.cachePath= KleeBot.config.getCacheDir() +File.separatorChar+cacheName;
    }
    public void store(){
        try {
            File cache=new File(cachePath);
            if (!cache.exists()){
                cache.createNewFile();
            }
            ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(cache));
            out.writeObject(this);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getCacheName() {
        return cacheName;
    }

    public long getExpired() {
        return expired;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public byte[] getContent() {
        return content;
    }

    public String getCachePath() {
        return cachePath;
    }

    public static Cache restore(String name){
        File cache=new File(name);
        try {
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(cache));
            return (Cache) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
