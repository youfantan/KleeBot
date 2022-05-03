package glous.kleebot.commands.impl;

import glous.kleebot.KleeBot;
import glous.kleebot.commands.ICommandExecutor;
import net.mamoe.mirai.contact.Group;

public class BroadcastCommand implements ICommandExecutor {
    @Override
    public boolean process(String command) {
        return command.startsWith("broadcast ");
    }

    @Override
    public boolean execute(String command) {
        String[] args=command.split(" ");
        if (args.length==2){
            String msg=args[1];
            for (Group g :
                    KleeBot.botInstance.getGroups()) {
                g.sendMessage(msg);
            }
            return true;
        }
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
