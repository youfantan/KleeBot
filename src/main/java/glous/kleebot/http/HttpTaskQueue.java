package glous.kleebot.http;

import glous.kleebot.async.BaseFunction;

import java.util.LinkedList;

public class HttpTaskQueue {
    private final LinkedList<BaseFunction> tasks=new LinkedList<>();
    private Thread[] threads;
    private HttpServer server;
    public HttpTaskQueue(int size,HttpServer server) {
        this.size = size;
        this.server=server;
        this.threads=new Thread[size];
    }
    public void start(){
        new Thread(()->{
            while (server.RUNNING_FLAG){
                while (!tasks.isEmpty()){
                    for (int i = 0; i < size; i++) {
                        Thread t=threads[i];
                        if ((t==null||!t.isAlive())){
                            BaseFunction function;
                            if ((function=tasks.pollFirst())!=null){
                                threads[i]=new Thread(()->{
                                    try {
                                        function.execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                threads[i].start();
                            }
                            break;
                        }
                    }
                }
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public synchronized void addTask(BaseFunction function){
        tasks.addLast(function);
        synchronized (this){
            notify();
        }
    }

    private int size;
}
