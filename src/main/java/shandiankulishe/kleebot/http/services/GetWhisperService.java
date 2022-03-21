package shandiankulishe.kleebot.http.services;

import com.google.gson.stream.JsonWriter;
import shandiankulishe.kleebot.http.HttpClient;
import shandiankulishe.kleebot.http.IWebService;

import java.io.IOException;
import java.io.StringWriter;

public class GetWhisperService implements IWebService {
    @Override
    public boolean doGET(HttpClient client) throws IOException {
        client.setHeader("Access-Control-Allow-Origin","*");
        String requestKey=client.getRequestPath();
        StringWriter out=new StringWriter();
        JsonWriter writer=new JsonWriter(out);
        writer.beginObject();
        if (!requestKey.contains(":")){
            writer.name("status").value("error");
            writer.name("message").value("request method not right");
            writer.endObject();
            writer.flush();
            writer.close();
            client.writeResponseBody(out.toString());
            client.finish();
            return true;
        }
        requestKey=requestKey.substring(requestKey.indexOf(":")+1);
        if (!StoreWhisperService.dataMap.containsKey(requestKey)){
            writer.name("status").value("error");
            writer.name("message").value("whisper not found");
            writer.endObject();
            writer.flush();
            writer.close();
            client.writeResponseBody(out.toString());
            client.finish();
            return true;
        }
        writer.name("status").value("ok");
        writer.name("message").value("successfully got data");
        writer.name("body").value(StoreWhisperService.dataMap.get(requestKey));
        StoreWhisperService.dataMap.remove(requestKey);
        writer.endObject();
        writer.flush();
        writer.close();
        client.writeResponseBody(out.toString());
        client.finish();
        return true;
    }

    @Override
    public boolean doPOST(HttpClient client) throws IOException {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
