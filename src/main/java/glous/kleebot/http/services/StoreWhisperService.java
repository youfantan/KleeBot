package glous.kleebot.http.services;

import com.google.gson.stream.JsonWriter;
import glous.kleebot.http.HttpClient;
import glous.kleebot.http.IWebService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

public class StoreWhisperService implements IWebService {
    public static HashMap<String,String> dataMap=new HashMap<>();
    @Override
    public boolean doGET(HttpClient client) throws IOException {
        return false;
    }

    @Override
    public boolean doPOST(HttpClient client) throws IOException {
        String key= UUID.randomUUID().toString();
        client.setHeader("Access-Control-Allow-Origin","*");
        String encryptedData=client.getBody();
        StringWriter out=new StringWriter();
        JsonWriter writer=new JsonWriter(out);
        writer.beginObject();
        if (encryptedData.length()>1024*1024*20){
            writer.name("status").value("error");
            writer.name("message").value("max size limit of 10MB");
            writer.endArray();
            writer.flush();
            writer.close();
            client.writeResponseBody(out.toString());
            client.finish();
            return true;
        }
        dataMap.put(key,encryptedData);
        writer.name("status").value("ok");
        writer.name("message").value("successfully stored");
        writer.name("uuid").value(key);
        writer.endObject();
        writer.flush();
        writer.close();
        client.writeResponseBody(out.toString());
        client.finish();
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
