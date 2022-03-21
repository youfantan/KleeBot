package shandiankulishe.kleebot.commands;

public interface ICommandExecutor {
    boolean process(String command);
    boolean execute(String command);
    void init();
    void stop();
}
