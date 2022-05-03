package glous.kleebot.services.impl;

import glous.kleebot.KleeBot;
import glous.kleebot.features.builtin.MCServerMOTD;
import glous.kleebot.features.minecraft.PingAPI;
import glous.kleebot.services.GroupService;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.util.Base64;

public class MCPingService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount())+" mcp ");
    }

    @Override
    public boolean execute(GroupMessageEvent event) throws Exception {
        String message=event.getMessage().serializeToMiraiCode();
        String[] args=message.split(" ");
        if (args.length==3){
            String rawHost=args[2].replace("\\","");//¿
            String host=rawHost;
            int port=25565;
            if (rawHost.contains(":")){
                host=rawHost.substring(0,rawHost.indexOf(":"));
                port=Integer.parseInt(rawHost.substring(rawHost.indexOf(":")+1));
            }
            PingAPI api=new PingAPI(host,port);
            MCServerMOTD motd=api.ping();
            if (motd.getStatus()==-1){
                sendMessage("错误的主机名或端口号",event);
            } else{
                String ret=
                        """
                                %s 的 服务器信息
                                服务器版本名: %s
                                服务器版本协议: %d
                                最大在线人数: %d
                                当前在线人数: %d
                                描述: %s
                                """.formatted(host+":"+port,motd.getName(),motd.getProtocol(),motd.getMaxPlayer(),motd.getOnlinePlayer(),motd.getDescription());
                MessageChainBuilder builder=new MessageChainBuilder();
                builder.append(new At(event.getSender().getId()));
                builder.append("\n").append(ret);
                ExternalResource res=ExternalResource.create(Base64.getDecoder().decode(motd.getFavicon().substring(motd.getFavicon().indexOf(",")+1)));
                Image image=event.getSubject().uploadImage(res);
                builder.append(image);
                res.close();
                event.getGroup().sendMessage(builder.build());
                return true;
            }
        }
        return false;
    }
}
