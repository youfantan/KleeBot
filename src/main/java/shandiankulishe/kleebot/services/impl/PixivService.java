package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.features.pixiv.PixivAPI;
import shandiankulishe.kleebot.services.GroupService;

import java.io.IOException;
import java.util.HashMap;

import static shandiankulishe.kleebot.utils.StringUtils.isDigit;

public class PixivService implements GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " pixiv");
    }
    @Override
    public void execute(GroupMessageEvent event) {
        String msg=event.getMessage().serializeToMiraiCode();
        String method=msg.substring(msg.indexOf("pixiv")+6);
        PixivAPI pixivAPI=new PixivAPI(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort());
        if (method.startsWith("rank")){//get rank information
            //get ranking serial
            if (!method.contains("#")||(!method.contains("daily")&&!method.contains("weekly")&&!method.contains("monthly"))){
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append(" 未定义用法。请输入 @KleeBot help 获取帮助。");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            String type=method.substring(method.indexOf("rank")+5,method.indexOf("#")-1);
            String serial=method.substring(method.indexOf("#")+1);
            if (!isDigit(serial)){
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append("  不是数字。");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            int serialNum=Integer.parseInt(serial);
            HashMap<Integer,HashMap<String,String>> ranking;
            if (type.equals("daily"))
                ranking=pixivAPI.getDailyRanking();
            else if (type.equals("weekly"))
                ranking=pixivAPI.getWeeklyRanking();
            else if (type.equals("monthly"))
                ranking=pixivAPI.getMonthlyRanking();
            else {
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append(" 排行榜仅提供daily/weekly/monthly选项。");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            if (serialNum>ranking.size()+1){
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append(" 超出榜单范围.");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            HashMap<String,String> artwork=ranking.get(serialNum+1);
            sendRankMessage(event, pixivAPI, artwork);
        } else{
            String illustid=method;
            if (!isDigit(illustid)){
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append("  不是数字。");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            HashMap<String,String> artwork=pixivAPI.getArtwork(Integer.parseInt(illustid));
            if (artwork==null){
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append("  作品不存在。");
                event.getGroup().sendMessage(builder.build());
                return;
            }
            sendRankMessage(event, pixivAPI, artwork);
        }
    }

    private void sendRankMessage(GroupMessageEvent event, PixivAPI pixivAPI, HashMap<String, String> artwork) {
        ExternalResource resource=ExternalResource.create(pixivAPI.getImage(artwork.get("imageUrl")));
        MessageChainBuilder builder=new MessageChainBuilder();
        Image image=event.getSubject().uploadImage(resource);
        builder.append(new At(event.getSender().getId()));
        builder.append(image);
        builder.append(
                """
                画师: %s
                上传时间: %s
                原图URL: %s
                """.formatted(artwork.get("author"),artwork.get("date"),artwork.get("imageUrl"))
        );
        event.getGroup().sendMessage(builder.build());
        try {
            resource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}