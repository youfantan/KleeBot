package glous.kleebot.services.impl;

import glous.kleebot.KleeBot;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.features.pixiv.PixivAPI;
import glous.kleebot.services.GroupService;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;

import java.util.Arrays;
import java.util.HashMap;

public class SyncService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" sync ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        String message=event.getMessage().serializeToMiraiCode();
        String[] args=message.substring(message.indexOf("sync")+5).split(" ");
        if (args[0].equals("info")){
            //pixiv rank sync info
            PixivAPI api=new PixivAPI(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort());
            HashMap<Integer, HashMap<String,String>> daily=api.getDailyRanking(false);
            HashMap<Integer,HashMap<String,String>> weekly=api.getWeeklyRanking(false);
            HashMap<Integer,HashMap<String,String>> monthly=api.getMonthlyRanking(false);
            StringBuilder builder=new StringBuilder();
            builder.append("Pixiv Synchronization Info:\n");
            for (HashMap<String, String> artwork :
                    daily.values()) {
                String syncInfo=
                        """
                                %s\040""".formatted(artwork.get("title"));
                builder.append(syncInfo);
                if (CacheFactory.getCache(artwork.get("imageUrl"))==null)
                    builder.append("\uD83D\uDD34\n");
                else
                    builder.append("\uD83D\uDFE2\n");
            }
            for (HashMap<String, String> artwork :
                    weekly.values()) {
                String syncInfo=
                        """
                                %s\040""".formatted(artwork.get("title"));
                builder.append(syncInfo);
                if (CacheFactory.getCache(artwork.get("imageUrl"))==null)
                    builder.append("\uD83D\uDD34\n");
                else
                    builder.append("\uD83D\uDFE2\n");
            }
            for (HashMap<String, String> artwork :
                    monthly.values()) {
                String syncInfo=
                        """
                                %s\040""".formatted(artwork.get("title"));
                builder.append(syncInfo);
                if (CacheFactory.getCache(artwork.get("imageUrl"))==null)
                    builder.append("\uD83D\uDD34\n");
                else
                    builder.append("\uD83D\uDFE2\n");
            }
            builder.append("END");
            System.out.println(builder.toString());
            sendMessage(builder.toString(),event);
            return true ;
        }
        return false;
    }
}
