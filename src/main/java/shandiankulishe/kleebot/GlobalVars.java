package shandiankulishe.kleebot;

import shandiankulishe.kleebot.async.AsyncTaskQueue;

public class GlobalVars {
    private static AsyncTaskQueue queue;

    public static AsyncTaskQueue getQueue() {
        return queue;
    }

    public static void setQueue(AsyncTaskQueue queue) {
        GlobalVars.queue = queue;
    }
}
