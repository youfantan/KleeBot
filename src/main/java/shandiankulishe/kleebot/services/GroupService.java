package shandiankulishe.kleebot.services;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public abstract class GroupService extends Service{
    public boolean process(GroupMessageEvent event){
        return false;
    }
    public boolean execute(GroupMessageEvent event) throws Exception{ return true; }
    public static void sendMessage(String msg, GroupMessageEvent event){
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append(" ");
        builder.append(msg);
        event.getGroup().sendMessage(builder.build());
    }
}
