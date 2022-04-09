package glous.kleebot.services.impl;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import org.bouncycastle.util.encoders.Hex;
import glous.kleebot.KleeBot;
import glous.kleebot.async.Task;
import glous.kleebot.async.Timer;
import glous.kleebot.features.pixiv.PixivAPI;
import glous.kleebot.http.services.StoreWhisperService;
import glous.kleebot.log.Logger;
import glous.kleebot.services.GroupService;
import glous.kleebot.utils.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static glous.kleebot.utils.StringUtils.isDigit;

public class PixivService extends GroupService {
    private static final PixivAPI api=new PixivAPI(KleeBot.config.getProxyHost(),KleeBot.config.getProxyPort());
    private static final Logger logger=Logger.getLogger(PixivService.class);
    private static void syncRanks(){
        try {
            HashMap<Integer,HashMap<String,String>> daily=api.getDailyRanking();
            HashMap<Integer,HashMap<String,String>> weekly=api.getWeeklyRanking();
            HashMap<Integer,HashMap<String,String>> monthly=api.getMonthlyRanking();
            logger.info("开始同步日榜");
            for (HashMap<String, String> artwork :
                    daily.values()) {
                logger.info(
                        """
                                正在同步: 标题: %s/作者: %s/日期: %s/URL: %s
                                """.formatted(artwork.get("title"),artwork.get("author"),artwork.get("date"),artwork.get("imageUrl")));
                PixivService.api.getImage(artwork.get("imageUrl"));
            }
            logger.info("开始同步周榜");
            for (HashMap<String, String> artwork :
                    weekly.values()) {
                logger.info(
                        """
                                正在同步: 标题: %s/作者: %s/日期: %s/URL: %s
                                """.formatted(artwork.get("title"),artwork.get("author"),artwork.get("date"),artwork.get("imageUrl")));
                PixivService.api.getImage(artwork.get("imageUrl"));
            }
            logger.info("开始同步月榜");
            for (HashMap<String, String> artwork :
                    monthly.values()) {
                logger.info(
                        """
                                正在同步: 标题: %s/作者: %s/日期: %s/URL: %s
                                """.formatted(artwork.get("title"),artwork.get("author"),artwork.get("date"),artwork.get("imageUrl")));
                PixivService.api.getImage(artwork.get("imageUrl"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize() {
        Timer.registerScheduledTask(new Task(PixivService::syncRanks,this.getServiceName()+"#syncRanks"),Timer.DAY);
        logger.info(getServiceName()+" initialized successfully");
    }

    @Override
    public boolean process(GroupMessageEvent event) {
        return event.getMessage().serializeToMiraiCode().startsWith(new At(KleeBot.config.getBotAccount()).serializeToMiraiCode() + " pixiv ");
    }
    @Override
    public boolean execute(GroupMessageEvent event) throws IOException {
        String msg=event.getMessage().serializeToMiraiCode();
        String method=msg.substring(msg.indexOf("pixiv")+6);
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
                ranking= api.getDailyRanking();
            else if (type.equals("weekly"))
                ranking= api.getWeeklyRanking();
            else if (type.equals("monthly"))
                ranking= api.getMonthlyRanking();
            else {
                return false;
            }
            if (serialNum>ranking.size()+1){
                return false;
            }
            HashMap<String,String> artwork=ranking.get(serialNum);
            sendRankMessage(event, artwork);
        } else{
            String illustid=method;
            if (!isDigit(illustid)){
                return false;
            }
            HashMap<String,String> artwork= api.getArtwork(Integer.parseInt(illustid)-1);
            if (artwork==null){
                return false;
            }
            sendRankMessage(event, artwork);
        }
        return true;
    }

    private void sendRankMessage(GroupMessageEvent event, HashMap<String, String> artwork) throws IOException {
        if (artwork.get("sexual").equals("true")){
            String uuid=UUID.randomUUID().toString();
            StoreWhisperService.dataMap.put(uuid, Hex.toHexString(Objects.requireNonNull(DigestUtils.AES128CBCPKCS7Encrypt(("image;data:image/jpg;base64," + Base64.getEncoder().encodeToString(PixivService.api.getImage(artwork.get("imageUrl")))).getBytes(StandardCharsets.UTF_8)))));
            sendMessage("富强民主文明和谐自由平等公正法制爱国敬业诚信友善。\n图片编号为: %s\n访问地址: http://%s/Whisper/".formatted(uuid,KleeBot.ip),event);
        } else{
            ExternalResource resource=ExternalResource.create(PixivService.api.getImage(artwork.get("imageUrl")));
            MessageChainBuilder builder=new MessageChainBuilder();
            Image image=event.getSubject().uploadImage(resource);
            builder.append(new At(event.getSender().getId()));
            builder.append(image);
            builder.append(
                    """
                    标题: %s
                    画师: %s
                    上传时间: %s
                    原图URL: %s
                    """.formatted(artwork.get("title"),artwork.get("author"),artwork.get("date"),artwork.get("imageUrl"))
            );
            event.getGroup().sendMessage(builder.build());
            resource.close();
        }
    }
}
