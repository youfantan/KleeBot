package shandiankulishe.kleebot.commands.impl;

import shandiankulishe.kleebot.GlobalVars;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.cache.CacheFactory;
import shandiankulishe.kleebot.commands.CommandRegistry;
import shandiankulishe.kleebot.commands.ICommandExecutor;
import shandiankulishe.kleebot.services.ServiceRegistry;

import java.io.File;
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
            GlobalVars.getQueue().stop();
            KleeBot.stop();
            CommandRegistry.stop();
            CacheFactory.serializeCaches();
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
