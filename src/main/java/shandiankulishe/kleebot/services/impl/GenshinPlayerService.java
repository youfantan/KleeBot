package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.features.builtin.PlayerInfo;
import shandiankulishe.kleebot.features.builtin.Role;
import shandiankulishe.kleebot.features.builtin.World;
import shandiankulishe.kleebot.features.genshin.GenshinAPI;
import shandiankulishe.kleebot.services.GroupService;
import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenshinPlayerService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" gsearch ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws IOException, FontFormatException, NoSuchAlgorithmException {
        String rawMessage=event.getMessage().serializeToMiraiCode();
        String uid=rawMessage.substring(rawMessage.indexOf("gsearch")+8);
        if ((uid=StringUtils.findDigit(uid))!=null){
            String cookie= FileUtils.readFile(KleeBot.config.getCookieFile(), StandardCharsets.UTF_8);
            if (cookie==null){
                sendErrorMessage(event,"米游社cookie文件不存在，请联系bot管理员修复。");
                return false;
            }
            GenshinAPI api=new GenshinAPI(cookie);
            PlayerInfo info;
            if (rawMessage.endsWith("CN")){
                info=api.getPlayerInfo(GenshinAPI.GENSHIN_CHINA,uid);
            } else if (rawMessage.endsWith("CN_B")){
                info=api.getPlayerInfo(GenshinAPI.GENSHIN_CHINA_BILIBILI,uid);
            } else{
                info=api.getPlayerInfo(GenshinAPI.GENSHIN_CHINA,uid);
            }
            if (info.getStatus()==0) {
                if (rawMessage.contains("img")){
                    String encodedImg=api.generatePlayerInfoImage(info);
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
                    builder.append("\n%s 的玩家信息:\n".formatted(uid));
                    builder.append("    角色:\n");
                    Role[] roles=info.getRoles();
                    for (Role r:roles){
                        builder.append("""
                            %s 等级: %s 好感度: %s
                            """.formatted(r.getName(),r.getLevel(),r.getFetter()));
                    }
                    builder.append("    大世界:\n");
                    World[] worlds=info.getWorlds();
                    for (World w:worlds){
                        int percentage=w.getExploration_percentage();
                        BigDecimal decimal=BigDecimal.valueOf(percentage);
                        decimal=decimal.divide(BigDecimal.valueOf(10),1, RoundingMode.HALF_UP);
                        builder.append("""
                            %s 探索度: %f 
                            """.formatted(w.getName(),decimal.doubleValue()));
                    }
                    builder.append("    统计:");
                    builder.append(
                            """
                                    
                                    风神瞳收集: %d
                                    岩神瞳收集: %d
                                    雷神瞳收集: %d
                                    普通宝箱收集: %d
                                    稀有宝箱收集: %d
                                    珍贵宝箱收集: %d
                                    华丽宝箱收集: %d
                                    奇馈宝箱收集: %d
                                    拥有角色数: %d
                                    成就数: %d
                                    活跃天数: %d
                                    深渊进度: %s
                                    """
                                    .formatted(
                                            info.getAnemoculus_number(),
                                            info.getGeoculus_number(),
                                            info.getElectroculus_number(),
                                            info.getCommon_chest_number(),
                                            info.getExquisite_chest_number(),
                                            info.getPrecious_chest_number(),
                                            info.getLuxurious_chest_number(),
                                            info.getMagic_chest_number(),
                                            info.getAvatar_number(),
                                            info.getAchievement_number(),
                                            info.getActive_day_number(),
                                            info.getSpiral_abyss()
                                    )
                    );
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
