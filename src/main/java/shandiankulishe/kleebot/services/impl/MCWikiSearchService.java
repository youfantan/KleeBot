package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.features.builtin.MCWikiElement;
import shandiankulishe.kleebot.features.minecraft.WikiAPI;
import shandiankulishe.kleebot.services.GroupService;

import java.io.IOException;
import java.util.Base64;

public class MCWikiSearchService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" mcws ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        String message=event.getMessage().serializeToMiraiCode();
        String item=message.substring(message.indexOf("mcws")+5);
        if (item.isEmpty()){
            return false;
        } else{
            WikiAPI api=new WikiAPI(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort());
            if (item.contains("CN")){
                if (item.contains("img")){
                    item=item.substring(0,item.indexOf(" "));
                    byte[] img=api.getElementImage(item,WikiAPI.WIKI_CN);
                    sendElementImage(event,img);
                    return true;
                } else{
                    if (item.contains(" ")){
                        item=item.substring(0,item.indexOf(" "));
                    }
                    MCWikiElement element=api.getElement(item,WikiAPI.WIKI_CN);
                    if (element==null){
                        sendMessage("未在minecraft wiki中找到 %s 条目".formatted(item),event);
                        return false;
                    }
                    sendElement(event, element);
                    return true;
                }
            } else{
                if (item.contains("img")){
                    item=item.substring(0,item.indexOf(" "));
                    byte[] img=api.getElementImage(item,WikiAPI.WIKI_EN);
                    sendElementImage(event,img);
                    return true;
                } else{
                    if (item.contains(" ")){
                        item=item.substring(0,item.indexOf(" "));
                    }
                    MCWikiElement element=api.getElement(item,WikiAPI.WIKI_EN);
                    if (element==null){
                        sendMessage("element %s not found in minecraft wiki".formatted(item),event);
                        return false;
                    }
                    sendElement(event, element);
                    return true;
                }
            }
        }
    }
    private void sendElementImage(GroupMessageEvent event, byte[] element) throws IOException {
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append(" ");
        ExternalResource resource=ExternalResource.create(element);
        Image image=event.getSubject().uploadImage(resource);
        builder.append(image);
        event.getGroup().sendMessage(builder.build());
        resource.close();

    }
    private void sendElement(GroupMessageEvent event, MCWikiElement element) throws IOException {
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append("\n").append(element.getDescription());
        builder.append("\n").append(element.getUrl());
        ExternalResource resource=ExternalResource.create(Base64.getDecoder().decode(element.getImage()));
        Image image=event.getSubject().uploadImage(resource);
        builder.append(image);
        event.getGroup().sendMessage(builder.build());
        resource.close();
    }
}
