package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.services.GroupService;

import java.net.URL;

public class GithubSpeedService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" ghr ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        String message=event.getMessage().serializeToMiraiCode();
        String _url=message.substring(message.indexOf("ghr")+4);
        URL url=new URL(_url);
        if (url.getHost().equals("raw.githubusercontent.com")){
            _url.replace("raw.githubusercontent.com","raw.staticdn.net");
        } else if (url.getHost().contains("github.com")){
            _url.replace(url.getHost(),"cdn.jsdelivr.net/gh");
            sendMessage(_url,event);
        } else {
            return false;
        }
        return true;
    }
}
