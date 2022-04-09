package glous.kleebot.plugin;

import glous.kleebot.BotConfig;
import glous.kleebot.config.Configuration;
import glous.kleebot.http.HttpServer;
import glous.kleebot.log.Logger;

public abstract class Plugin {
    protected final Logger logger=Logger.getLogger(this.getClass());
    protected String name=this.getClass().getName();
    public void onInit(Configuration configuration, BotConfig config, HttpServer server){
    }
    public void onStop(Configuration configuration, BotConfig config){
    }
}
