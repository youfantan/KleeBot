package glous.kleebot.config;

public class SingleLineCommentNode extends ConfigNode{
    public SingleLineCommentNode(String key, Object value, String comment) {
        super(key, value, comment);
    }

    @Override
    public String getKey() {
        return "__SINGLE_LINE_COMMENT";
    }

    @Override
    public void setKey(String key) {
    }

    @Override
    public Object getValue() {
        return "__SINGLE_LINE_COMMENT";
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public String getComment() {
        return super.getComment();
    }

    @Override
    public void setComment(String comment) {
        super.setComment(comment);
    }

    @Override
    public String toString() {
        return "# "+getComment();
    }
}
