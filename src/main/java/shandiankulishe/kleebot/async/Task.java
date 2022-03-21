package shandiankulishe.kleebot.async;

import java.util.Objects;
import java.util.UUID;

public class Task {
    @Override
    public boolean equals(Object o) {
        if (o instanceof Task task){
            return (task.getName().equals(this.name)) && (task.getFullName().equals(this.getFullName())) && (task.getFunc() == this.func);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(func, name);
    }

    public Task(BaseFunction func, String name) {
        this.func = func;
        this.name = name;
    }

    public BaseFunction getFunc() {
        return func;
    }

    public String getName() {
        return name;
    }

    private BaseFunction func;
    private String name;
    public String getFullName(){
        return name+"@"+ UUID.randomUUID();
    }
}
