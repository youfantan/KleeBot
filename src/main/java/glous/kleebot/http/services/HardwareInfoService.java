package glous.kleebot.http.services;

import glous.kleebot.services.api.HardwareInfo;
import glous.kleebot.http.HttpClient;
import glous.kleebot.http.IWebService;

import java.io.IOException;

public class HardwareInfoService implements IWebService {
    @Override
    public boolean doGET(HttpClient client) throws IOException{
        client.setHeader("Content-Type","application/json");
        client.setHeader("Access-Control-Allow-Origin","*");
        client.setResponseCode("200 OK");
        client.writeResponseBody(HardwareInfo.getInstance().getJsonFormattedInfo());
        client.finish();
        return true;
    }

    @Override
    public boolean doPOST(HttpClient client) {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
