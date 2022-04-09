package glous.kleebot.services;

import net.mamoe.mirai.event.events.FriendMessageEvent;

public interface FriendService {
    boolean process(FriendMessageEvent event);
    void execute(FriendMessageEvent event);
}
