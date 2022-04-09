package glous.kleebot.config;

public class ConfigValue {
    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ConfigValue(Object val, String comment) {
        this.val = val;
        this.comment = comment;
    }

    private Object val;
    private String comment;
}
