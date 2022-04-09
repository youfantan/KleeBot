package glous.kleebot.commands;

import glous.kleebot.log.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandRegistry {
    private static HashMap<String,ICommandExecutor> commandMap=new HashMap<>();
    private static Logger logger= Logger.getLogger(CommandRegistry.class);
    public static void register(String name,ICommandExecutor executor){
        commandMap.put(name,executor);
    }
    public static void start(){
        new Thread(()->{
            Scanner scanner=new Scanner(System.in);
            while (scanner.hasNext()){
                String command=scanner.nextLine();
                for (Map.Entry<String,ICommandExecutor> e:
                     commandMap.entrySet()) {
                    if (e.getValue().process(command)){
                        if (!e.getValue().execute(command)){
                            logger.error("Command Executed Error: %s/%s".formatted(e.getKey(),e.getValue().getClass().getName()));
                        }
                    }
                }
            }
        }).start();
    }
    public static void stop(){
        for (ICommandExecutor executor :
                commandMap.values()) {
            executor.stop();
        }
    }
}
