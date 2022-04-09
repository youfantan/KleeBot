package glous.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import glous.kleebot.KleeBot;
import glous.kleebot.services.GroupService;

public class GenshinArtifactScoreService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " gscore");
    }

    @Override
    public boolean execute(GroupMessageEvent event) {
//        MessageChain chain=event.getMessage();
//        Image image = (Image) chain.stream().filter(Image.class::isInstance).findFirst().orElse(null);
//        byte[] imageBuffer= FileUtils.download(Image.queryUrl(image));
//        String cacheName=CacheFactory.getCacheName();
//        FileUtils.writeFile("cache"+ File.separatorChar+cacheName+".jpg",imageBuffer);
//        event.getGroup().sendMessage("正在解析图片，根据服务器性能需要1-5秒不等");
//        event.getGroup().sendMessage(ArtifactScore.ScoreFactory("cache/"+cacheName+".jpg"));
        return false;
    }
}
