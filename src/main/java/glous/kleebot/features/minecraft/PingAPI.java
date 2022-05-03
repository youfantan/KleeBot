package glous.kleebot.features.minecraft;

import com.google.gson.*;
import glous.kleebot.features.builtin.MCPacket;
import glous.kleebot.features.builtin.MCServerMOTD;
import glous.kleebot.features.builtin.VariableInt;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PingAPI {
    String host;
    int port;
    public PingAPI(String host,int port){
        this.host=host;
        this.port=port;
    }
    public MCServerMOTD ping() throws IOException {
        MCServerMOTD motd=new MCServerMOTD();
        try(Socket socket=new Socket(host,port)){
            motd.setStatus(0);
            DataInputStream in=new DataInputStream(socket.getInputStream());
            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
            MCPacket packet=new MCPacket();
            packet.setPacketID(0x00);
            packet.writeVarInt(755);
            packet.writeVarInt(host.length());
            packet.writeBytes(host.getBytes(StandardCharsets.UTF_8));
            packet.writeShort((short) port);
            packet.writeVarInt(1);
            out.write(packet.getPacket());
            out.write(MCPacket.getRequestPacket().getPacket());
            VariableInt vi1=new VariableInt();
            int i1=vi1.readBytes(in);
            VariableInt vi2=new VariableInt();
            int i2=vi2.readBytes(in);
            //i1,i2 may be used in the future
            VariableInt vi3=new VariableInt();
            int i3=vi3.readBytes(in);
            byte[] resp=new byte[i3];
            in.readFully(resp);
            String response=new String(resp,StandardCharsets.UTF_8);
            JsonObject object= JsonParser.parseString(response).getAsJsonObject();
            String desText;
            if (object.get("description").isJsonObject()){
                JsonObject description=object.get("description").getAsJsonObject();
                if (description.has("extra")){
                    JsonArray array=description.get("extra").getAsJsonArray();
                    desText=traverseGetMotd(new StringWriter(),array);
                } else{
                    desText=reformatMOTD(description.get("text").getAsString());
                }
            } else{
                desText=reformatMOTD(object.get("description").getAsString());
            }
            JsonObject version=object.get("version").getAsJsonObject();
            String name=version.get("name").getAsString();
            int protocol=version.get("protocol").getAsInt();
            String favicon=object.get("favicon").getAsString();
            JsonObject players=object.get("players").getAsJsonObject();
            int max=players.get("max").getAsInt();
            int online=players.get("online").getAsInt();
            motd.setDescription(desText);
            motd.setOnlinePlayer(online);
            motd.setMaxPlayer(max);
            motd.setName(name);
            motd.setProtocol(protocol);
            motd.setFavicon(favicon);
            in.close();
            out.close();
        } catch (IOException e){
            motd.setStatus(-1);
            e.printStackTrace();
        }
        return motd;
    }
    public String traverseGetMotd(StringWriter writer,JsonArray array){
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj=array.get(i).getAsJsonObject();
            if (obj.has("extra")){
                traverseGetMotd(writer,obj.get("extra").getAsJsonArray());
            } else{
                String text=obj.get("text").getAsString();
                writer.write(text);
            }
        }
        return writer.toString();
    }
    public String reformatMOTD(String raw){
        CharArrayWriter writer=new CharArrayWriter();
        int status=0;
        /*
         * 0 > normal
         * 1 > §*
         * 2 > \
         * 3 > u
         * 4 > [\\u]*
         * 5 > [\\u]**
         * 6 > [\\u]***
         * */
        for (char c :
                raw.toCharArray()) {
            switch (status){
                case 0:
                    if (c=='§'){
                        status=1;
                    } else if (c=='\\'){
                        status=2;
                    } else{
                        writer.write(c);
                    }
                    break;
                case 1:
                    status=0;
                    break;
                case 2:
                    if (c=='u')
                        status++;
                    break;
                case 3:
                case 5:
                    status++;
                    break;
                case 4:
                    status++;
                    break;
                case 6:
                    status=0;
                    break;
            }
        }
        return writer.toString();
    }
}
