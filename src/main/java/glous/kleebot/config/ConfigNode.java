package glous.kleebot.config;

public class ConfigNode {
    public ConfigNode(String key, Object value, String comment) {
        this.key = key;
        this.value = value;
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getComment() {
        if (comment==null){
            return "";
        }
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Key : %s Value : %s(Type: %s) Comment: %s".formatted(key,value,value.getClass().getName(),comment);
    }

    private String key;
    private Object value;
    private String comment;
}
