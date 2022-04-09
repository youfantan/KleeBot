package glous.kleebot.commands.impl;

import glous.kleebot.GlobalVars;
import glous.kleebot.KleeBot;
import glous.kleebot.cache.CacheFactory;
import glous.kleebot.commands.CommandRegistry;
import glous.kleebot.commands.ICommandExecutor;
import glous.kleebot.http.ChromeInstance;
import glous.kleebot.log.Logger;
import glous.kleebot.plugin.Plugin;
import glous.kleebot.services.ServiceRegistry;

import java.io.IOException;

public class StopCommand implements ICommandExecutor {
    @Override
    public boolean process(String command) {
        return command.equals("stop");
    }

    @Override
    public boolean execute(String command) {
        try {
            ServiceRegistry.stop();
            KleeBot.plugins.forEach((Plugin plg)->{
                plg.onStop(KleeBot.configurationInstance,KleeBot.config);
            });
            KleeBot.getServerInstance().stop();
            KleeBot.botInstance.close();
            GlobalVars.getQueue().stop();
            KleeBot.stop();
            CommandRegistry.stop();
            CacheFactory.serializeCaches();
            ChromeInstance.stop();
            KleeBot.configurationInstance.mergeClass(KleeBot.config);
            KleeBot.configurationInstance.saveToFile();
            Logger.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
