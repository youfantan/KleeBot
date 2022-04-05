package shandiankulishe.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import org.bouncycastle.util.encoders.Hex;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.features.pixiv.PixivAPI;
import shandiankulishe.kleebot.http.services.StoreWhisperService;
import shandiankulishe.kleebot.services.GroupService;
import shandiankulishe.kleebot.utils.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static shandiankulishe.kleebot.utils.StringUtils.isDigit;

public class PixivService extends GroupService {
    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " pixiv ");
    }
    @Override
    public boolean execute(GroupMessageEvent event) throws IOException {
        String msg=event.getMessage().serializeToMiraiCode();
        String method=msg.substring(msg.indexOf("pixiv")+6);
        PixivAPI pixivAPI=new PixivAPI(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort());
        if (method.startsWith("rank")){//get rank information
            //get ranking serial
            if (!method.contains("#")||(!method.contains("daily")&&!method.contains("weekly")&&!method.contains("monthly"))){
                return false;
            }
            String type=method.substring(method.indexOf("rank")+5,method.indexOf("#")-1);
            String serial=method.substring(method.indexOf("#")+1);
            if (!isDigit(serial)){
                return false;
            }
            int serialNum=Integer.parseInt(serial);
            HashMap<Integer,HashMap<String,String>> ranking;
            if (type.equals("daily"))
                ranking=pixivAPI.getDailyRanking();
            else if (type.equals("weekly"))
                ranking=pixivAPI.getWeeklyRanking();
            else if (type.equals("monthly"))
                ranking=pixivAPI.getMonthlyRanking();
            else {
                return false;
            }
            if (serialNum>ranking.size()+1){
                return false;
            }
            HashMap<String,String> artwork=ranking.get(serialNum+1);
            sendRankMessage(event, pixivAPI, artwork);
        } else{
            String illustid=method;
            if (!isDigit(illustid)){
                return false;
            }
            HashMap<String,String> artwork=pixivAPI.getArtwork(Integer.parseInt(illustid));
            if (artwork==null){
                return false;
            }
            sendRankMessage(event, pixivAPI, artwork);
        }
        return true;
    }

    private void sendRankMessage(GroupMessageEvent event, PixivAPI pixivAPI, HashMap<String, String> artwork) throws IOException {
        if (artwork.get("sexual").equals("true")){
            String uuid=UUID.randomUUID().toString();
            StoreWhisperService.dataMap.put(uuid, Hex.toHexString(Objects.requireNonNull(DigestUtils.AES128CBCPKCS7Encrypt(("image;data:image/jpg;base64," + Base64.getEncoder().encodeToString(pixivAPI.getImage(artwork.get("imageUrl")))).getBytes(StandardCharsets.UTF_8)))));
            sendMessage("富强民主文明和谐自由平等公正法制爱国敬业诚信友善。\n图片编号为: %s\n访问地址: http://%s/Whisper/".formatted(uuid,KleeBot.ip),event);
        } else{
            ExternalResource resource=ExternalResource.create(pixivAPI.getImage(artwork.get("imageUrl")));
            MessageChainBuilder builder=new MessageChainBuilder();
            Image image=event.getSubject().uploadImage(resource);
            builder.append(new At(event.getSender().getId()));
            builder.append(image);
            builder.append(
                    """
                    画师: %s
                    上传时间: %s
                    原图URL: %s
                    """.formatted(artwork.get("author"),artwork.get("date"),artwork.get("imageUrl"))
            );
            event.getGroup().sendMessage(builder.build());
            resource.close();
        }
    }
}
