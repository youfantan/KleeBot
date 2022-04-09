package glous.kleebot.services.impl;

import glous.kleebot.features.builtin.AbyssFloor;
import glous.kleebot.features.builtin.AbyssInfo;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import glous.kleebot.KleeBot;
import glous.kleebot.features.genshin.GenshinAPI;
import glous.kleebot.services.GroupService;
import glous.kleebot.utils.FileUtils;
import glous.kleebot.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GenshinAbyssService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" gabyss ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        String rawMessage=event.getMessage().serializeToMiraiCode();
        String uid=rawMessage.substring(rawMessage.indexOf("gabyss")+7);
        if ((uid= StringUtils.findDigit(uid))!=null){
            String cookie= FileUtils.readFile(KleeBot.config.getCookieFile(), StandardCharsets.UTF_8);
            if (cookie==null){
                sendErrorMessage(event,"米游社cookie文件不存在，请联系bot管理员修复。");
                return false;
            }
            GenshinAPI api=new GenshinAPI(cookie);
            AbyssInfo info;
            if (rawMessage.endsWith("CN")){
                info=api.getSpiralAbyssInfo(GenshinAPI.GENSHIN_CHINA,uid);
            } else if (rawMessage.endsWith("CN_B")){
                info=api.getSpiralAbyssInfo(GenshinAPI.GENSHIN_CHINA_BILIBILI,uid);
            } else{
                info=api.getSpiralAbyssInfo(GenshinAPI.GENSHIN_CHINA,uid);
            }
            if (info.getStatus()==0) {
                if (rawMessage.contains("img")){
                    String encodedImg=api.generateAbyssInfoImage(info);
                    ExternalResource resource=ExternalResource.create(Base64.getDecoder().decode(encodedImg));
                    Image image=event.getSubject().uploadImage(resource);
                    MessageChainBuilder builder=new MessageChainBuilder();
                    builder.append(new At(event.getSender().getId()));
                    builder.append(image);
                    event.getGroup().sendMessage(builder.build());
                    resource.close();
                } else {
                    MessageChainBuilder builder=new MessageChainBuilder();
                    builder.append(new At(event.getSender().getId()));
                    builder.append("\n%s 的深渊信息:\n".formatted(uid));
                    builder.append(
                            """
                                    最深抵达: %s
                                    总渊星数: %d
                                    出战次数: %d
                                    最多击败数: %d
                                    最大伤害数: %d
                                    最大元素爆发数 %d
                                    最大元素战技数: %d
                                    """.formatted(info.getMax_floor(),info.getTotal_star(),info.getTotal_battle_times(),info.getMax_defeat(),info.getMax_damaged(),info.getMax_energy_skill(),info.getMax_skill())
                    );
                    builder.append("单层数据:\n");
                    for (AbyssFloor floor:info.getFloors()){
                        int total=floor.getMax_star();
                        int get=floor.getStar();
                        float progress=(float) get/total;
                        builder.append("\t层数: %d 获取渊星数: %d/%d 进度: %.2f\n".formatted(floor.getIndex(),get,total,progress*100));
                    }
                    event.getGroup().sendMessage(builder.build());
                }
            } else{
                sendErrorMessage(event,"发生错误，错误信息:%s".formatted(info.getErrorMsg()));
                return false;
            }
        }
        return true;
    }
    private void sendErrorMessage(GroupMessageEvent event,String errorMessage){
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append(" 无法通过UID获取玩家信息。错误信息:\n%s".formatted(errorMessage));
        event.getGroup().sendMessage(builder.build());
    }
}
