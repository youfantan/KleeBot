package shandiankulishe.kleebot.commands.impl;

import shandiankulishe.kleebot.GlobalVars;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.commands.CommandRegistry;
import shandiankulishe.kleebot.commands.ICommandExecutor;
import shandiankulishe.kleebot.http.ChromeInstance;
import shandiankulishe.kleebot.log.Logger;
import shandiankulishe.kleebot.services.ServiceRegistry;

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
