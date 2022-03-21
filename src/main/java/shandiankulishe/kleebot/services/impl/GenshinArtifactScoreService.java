package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.services.GroupService;
import shandiankulishe.kleebot.services.api.ArtifactScore;
import shandiankulishe.kleebot.utils.FileUtils;

import java.io.File;

public class GenshinArtifactScoreService implements GroupService {
    private static final Logger logger= LogManager.getLogger(GenshinArtifactScoreService.class);
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " gscore");
    }

    @Override
    public void execute(GroupMessageEvent event) {
        MessageChain chain=event.getMessage();
        Image image = (Image) chain.stream().filter(Image.class::isInstance).findFirst().orElse(null);
        byte[] imageBuffer= FileUtils.download(Image.queryUrl(image));
        String cacheName=CacheFactory.getCacheName();
        FileUtils.writeFile("cache"+ File.separatorChar+cacheName+".jpg",imageBuffer);
        event.getGroup().sendMessage("正在解析图片，根据服务器性能需要1-5秒不等");
        event.getGroup().sendMessage(ArtifactScore.ScoreFactory("cache/"+cacheName+".jpg"));
    }
}
