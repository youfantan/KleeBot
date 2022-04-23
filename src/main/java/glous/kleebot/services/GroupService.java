package glous.kleebot.services;

import glous.kleebot.KleeBot;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public abstract class GroupService extends Service{
    public boolean process(GroupMessageEvent event){
        return false;
    }
    public boolean execute(GroupMessageEvent event) throws Exception{ return true; }
    private boolean atTrigger(String raw,String cond){
        return raw.startsWith(new At(KleeBot.config.getBotAccount())+cond);
    }
    private boolean containsTrigger(String raw,String cond){
        return raw.contains(cond);
    }
    private boolean anyCondition(boolean ...args){
        boolean cond=false;
        for (boolean arg :
                args) {
            if (arg) {
                cond=true;
            }
        }
        return cond;
    }
    private boolean allCondition(boolean ...args){
        boolean cond=true;
        for (boolean arg:
                args) {
            if (!arg) {
                cond=false;
            }
        }
        return cond;
    }
    public static void sendMessage(String msg, GroupMessageEvent event){
        MessageChainBuilder builder=new MessageChainBuilder();
        builder.append(new At(event.getSender().getId()));
        builder.append(" ");
        builder.append(msg);
        event.getGroup().sendMessage(builder.build());
    }
}
