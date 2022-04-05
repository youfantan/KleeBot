package shandiankulishe.kleebot.async;
import shandiankulishe.kleebot.log.Logger;

public class AsyncTask implements Runnable{
    private Logger logger= Logger.getLogger(AsyncTask.class);
    private int state=0;
    private Task task;
    public AsyncTask(Task task){
        this.state=AsyncTaskQueue.TASK_PROCESSING;
        this.task=task;
    }

    public String getTaskName(){
        if (state==AsyncTaskQueue.TASK_PROCESSING){
            return task.getFullName();
        }
        return null;
    }

    public int getTaskState() {
        return state;
    }

    @Override
    public void run() {
        logger.trace("Start to execute Task: %s".formatted(task.getFullName()));
        try {
            task.getFunc().execute();
        } catch (Exception e){
            e.printStackTrace();
        }
        logger.trace("Task :%s executed done".formatted(task.getFullName()));
        this.state=AsyncTaskQueue.TASK_FINISHED;
    }
}
