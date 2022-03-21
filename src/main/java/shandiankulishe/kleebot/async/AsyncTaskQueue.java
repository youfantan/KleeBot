package shandiankulishe.kleebot.async;

import okhttp3.internal.concurrent.TaskQueue;
import shandiankulishe.kleebot.KleeBot;

import java.util.*;

public class AsyncTaskQueue {
    public static final int TASK_FINISHED =0;
    public static final int TASK_PROCESSING=1;
    boolean flag=true;
    final Vector<Task> taskQueue=new Vector<>();
    private AsyncTask[] threads;
    synchronized Task pollFirst(){
        if (taskQueue.size()>0){
            Task task=taskQueue.get(0);
            taskQueue.remove(0);
            return task;
        }
        return null;
    }
    public AsyncTaskQueue(int size){
        //init all working processes
        threads=new AsyncTask[size];
    }
    public void start(){
        while (KleeBot.getRunningFlag()&&flag){
            Task task;
            if ((task=pollFirst())!=null){
                //dispatch task
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i]==null||threads[i].getTaskState()==TASK_FINISHED){
                        threads[i]=new AsyncTask(task);
                        new Thread(threads[i]).start();
                        break;
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public List<String> getAllRunningTasksName(){
        List<String> taskNames=new ArrayList<>();
        for (AsyncTask task:threads){
            if (task!=null){
                if (task.getTaskState()==TASK_PROCESSING) {
                    taskNames.add(task.getTaskName());
                }
            }

        }
        return taskNames;
    }
    public void addTask(BaseFunction func,String funcName){
        taskQueue.add(new Task(func,funcName));
    }
    public void stop(){
        flag=false;
    }
}
