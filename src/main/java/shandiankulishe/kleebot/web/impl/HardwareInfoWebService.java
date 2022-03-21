package shandiankulishe.kleebot.web.impl;

import shandiankulishe.kleebot.services.api.HardwareInfo;
import shandiankulishe.kleebot.web.HttpClient;
import shandiankulishe.kleebot.web.WebService;

import java.io.IOException;

public class HardwareInfoWebService implements WebService {
    @Override
    public void initialize() {

    }

    @Override
    public void response(HttpClient client) {
        client.setHeader("Content-Type","application/json");
        client.setHeader("Access-Control-Allow-Origin","*");
        client.setResponseCode("200 OK");
        try {
            client.write(HardwareInfo.getInstance().getJsonFormattedInfo());
            client.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shut() {

    }
}
