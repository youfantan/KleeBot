package glous.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import glous.kleebot.KleeBot;
import glous.kleebot.services.GroupService;

import java.io.IOException;
import java.util.Objects;

public class HelpService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().equals(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " help");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws IOException {
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(
                """
                Copyright 2022 shandiankulishe@gmail.com
                官方仓库:www.github.com/youfantan/KleeBot
                有疑问请提交ISSUES，作者七月前不接收QQ消息
                (正在填坑，前有!符号代表正在开发)
                @[bot] help/? --显示帮助 例: @KleeBot help
                @[bot] status --获取Bot当前状态
                pixiv(注:r18作品会返回一个url，可以点击url后在本地浏览器执行javascript解密图片):
                @[bot] pixiv rank daily #[num] --获取pixiv日榜作品 例: @KleeBot rank daily #1
                @[bot] pixiv rank weekly #[num] --获取pixiv周榜作品 例: @KleeBot rank weekly #1
                @[bot] pixiv rank monthly #[num] --获取pixiv月榜作品 例: @KleeBot rank monthly #1
                @[bot] pixiv [artworkId] --根据artworkId获取作品 例: @KleeBot 96311355
                bilibili:
                @[bot] [bilibili稿件url] 获取稿件标题、封面、作者 例: @KleeBot https://www.bilibili.com/video/BV1GJ411x7h7
                *https://www.bilibili.com/* 直接从消息中检测稿件，返回同上 例: You got rick rolled lol https://www.bilibili.com/video/BV1GJ411x7h7
                原神(仅支持天空岛(官服)&世界树(b服)):
                !@[bot] gscore [图片] --根据图片获取圣遗物评分 例: @KleeBot gscore 
                """
        );
        ExternalResource gscoreexpimg=ExternalResource.create(Objects.requireNonNull(this.getClass().getResourceAsStream("/genshin.score.example.png")));
        Image gscoreexp=event.getSubject().uploadImage(gscoreexpimg);
        builder.append(gscoreexp);
        builder.append("\n");
        builder.append(
                """
                @[bot] gsearch [uid] [server] --根据玩家UID获取详细信息(角色/武器/圣遗物/蒙德、璃月、稻妻、渊下宫探索度) 国服/B服(暂不支持海外服务器(Oversea Server)) CN为官服 CN_B为b服 例: @KleeBot gsearch 128035175 CN
                !@[bot] gabyss [uid] --根据玩家UID获取深渊战绩 例: @KleeBot gabyss 128035715
                Minecraft:
                @[bot] mcws [条目] --获取minecraft wiki中条目的简介
                Github:
                @[bot] ghr [url] --获取github release的加速url
                自动推送:
                @[bot] enable-mcv --启用自动推送服务(Release&Snapshot) 例: @KleeBot enable mcv
                可选推送内容:
                - mcv: minecraft新版本(Release&Snapshot)(version_manifest.json及mojira，不会重复推送)
                """
        );
        event.getGroup().sendMessage(builder.build());
        gscoreexpimg.close();
        return true;
    }

    @Override
    public void initialize() {

    }
}
