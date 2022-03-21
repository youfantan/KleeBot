package shandiankulishe.kleebot.services;

import net.mamoe.mirai.event.events.GroupMessageEvent;

public interface GroupService extends Service{
    boolean process(GroupMessageEvent event);
    void execute(GroupMessageEvent event);
}
