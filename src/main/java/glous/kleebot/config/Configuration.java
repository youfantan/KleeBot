package glous.kleebot.config;

import glous.kleebot.utils.FileUtils;
import glous.kleebot.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Configuration {
    private static final int STATUS_KEY=0;
    private static final int STATUS_VALUE=1;
    private static final int STATUS_COMMENT=2;
    private File configFile;
    private String configContent;
    private Map<String,ConfigNode> configNodes;
    public Configuration(){
    }
    public Configuration(File file){
        this.configFile=file;
    }
    public String toString(){
        StringBuilder builder=new StringBuilder();
        for (Map.Entry<String,ConfigNode> entry :
                configNodes.entrySet()) {
            builder.append("Key : %s Value: %s(%s) Comment: %s\n".formatted(entry.getKey(),entry.getValue().getValue(),entry.getValue().getValue().getClass().getName(),entry.getValue().getComment()));
        }
        return builder.toString();
    }
    public void load(File file) throws IOException {
        this.configFile=file;
        this.load();
    }
    public void load(String content) throws IOException {
        this.configContent=content;
        this.parse();
    }
    public void load() throws IOException {
        this.parse();
    }

    public Map<String, ConfigNode> getConfigNodes() {
        return configNodes;
    }

    public <T> T serializeToClass(Class clz){
        try {
            Object obj=clz.getConstructors()[0].newInstance();
            Field[] fields=clz.getDeclaredFields();
            for (Field f :
                    fields) {
                f.setAccessible(true);
                ConfigNode node=getNode(f.getName());
                if (node!=null) {
                    f.set(obj,node.getValue());
                }
            }
            return (T) obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void mergeClass(Object obj){
        //get data struct
        Field[] fields=obj.getClass().getDeclaredFields();
        for (Field f :
                fields) {
            f.setAccessible(true);
            try {
                ConfigNode node=getNode(f.getName());
                setValue(f.getName(),f.get(obj),node.getComment());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    public String save(){
        StringBuilder builder=new StringBuilder();
        for (Map.Entry<String, ConfigNode> entry :
                configNodes.entrySet()) {
            if (entry.getValue().getKey().startsWith("__SINGLE_LINE_COMMENT")&&entry.getValue().getValue().toString().startsWith("__SINGLE_LINE_COMMENT"))
                 builder.append("# ").append(entry.getValue().getComment()).append("\n");
            else
                if (!entry.getValue().getComment().isEmpty())
                    builder.append(entry.getValue().getKey()).append(" : ").append(entry.getValue().getValue()).append(" #").append(entry.getValue().getComment()).append("\n");
                else
                    builder.append(entry.getValue().getKey()).append(" : ").append(entry.getValue().getValue()).append("\n");
        }
        return builder.toString();
    }
    public void saveToFile() throws IOException {
        if (configFile!=null){
            FileUtils.writeFile(configFile.getAbsolutePath(),save());
        } else{
            exception(0,0,"saveToFile() can only be used in load(java.io.File) method.");
        }
    }
    public boolean contains(String key){
        return getNode(key)!=null;
    }
    public void setValue(String key,ConfigNode node){
        this.configNodes.put(key, node);
    }
    public void setValue(String key,Object value,String comment){
        if(key.equals("")&&value.equals("")&&!comment.equals(""))
            this.setValue("__SINGLE_LINE_COMMENT"+comment, new ConfigNode("__SINGLE_LINE_COMMENT"+comment, "__SINGLE_LINE_COMMENT"+comment, comment));
        else
            this.setValue(key, new ConfigNode(key, value, comment));
    }
    public void setValue(String key,Object value){
            this.setValue(key,value,"");
    }
    public <T> T get(String key) throws IOException {
        return (T)getObject(key);
    }
    public ConfigNode getNode(String key){
        for (Map.Entry<String, ConfigNode> entry:
        this.configNodes.entrySet()){
            if (entry.getKey().equals(key))
                return entry.getValue();
        }
        return null;
    }
    public String getComment(String key) throws IOException {
        ConfigNode node=getNode(key);
        if (node==null){
            exception(0,0,"%s not found".formatted(key));
        }
        return node.getComment();
    }
    public int getInt(String key) throws IOException {
        Object obj=getObject(key);
        if (obj instanceof Integer){
            return (int) obj;
        } else {
            exception(0,0,"%s isn't an instance of Integer".formatted(key));
        }
        return 0;
    }
    public String getString(String key) throws IOException {
        Object obj=getObject(key);
        if (obj instanceof String){
            return (String) obj;
        } else {
            exception(0,0,"%s isn't an instance of String".formatted(key));
        }
        return null;
    }
    public float getFloat(String key) throws IOException {
        Object obj=getObject(key);
        if (obj instanceof Float){
            return (Float) obj;
        } else {
            exception(0,0,"%s isn't an instance of Float".formatted(key));
        }
        return 0;
    }
    public boolean getBoolean(String key) throws IOException {
        Object obj=getObject(key);
        if (obj instanceof Boolean){
            return (Boolean) obj;
        } else {
            exception(0,0,"%s isn't an instance of Boolean".formatted(key));
        }
        return false;
    }
    @Nullable
    private Object getObject(String key) throws IOException {
        //impl linear search so change the data struct
        ConfigNode node;
        if ((node=getNode(key))!=null){
            Object obj=node.getValue();
            if (obj==null){
                exception(0,0,"%s not found".formatted(key));
            }
            return obj;
        } else
            return null;
    }
    private void exception(int line,int col,String msg) throws IOException {
        throw new IOException("at line: %d col: %d:\n%s".formatted(line,col,msg));
    }
    private void parse() throws IOException {
        if (configContent==null){
            configContent= FileUtils.readFile(this.configFile.getAbsolutePath(), StandardCharsets.UTF_8);
        }
        int STATUS=STATUS_KEY;
        char[] chars=configContent.toCharArray();
        int line=0;
        int col=0;
        Map<String,ConfigNode> nodes=new LinkedHashMap<>();
        String key="";
        String val="";
        String comment="";
        int serialCode=0;
        for (char c :
                chars) {
            col++;
            //judge status
            switch (STATUS){
                case STATUS_KEY:
                {
                    switch (c) {
                        case ':' -> STATUS = STATUS_VALUE;
                        case '#' -> STATUS = STATUS_COMMENT;
                        default -> {
                            if (c != ' ') {
                                key += c;
                            }
                            break;
                        }
                    }
                    break;
                }
                case STATUS_VALUE:
                {
                    if (c == '\n') {
                        if (val.length()>1&&val.charAt(val.length()-1)=='\r'){
                            val=val.substring(0,val.length()-1);
                        }
                        if (!val.equals("")||!key.equals("")) {
                            Object oVal = getVal(val);
                            nodes.put(key,new ConfigNode(key,oVal,comment));
                        }
                        //judge if is a single line comment
                        if (val.equals("") && key.equals("")) {
                            //save as a single line comment
                            nodes.put("__SINGLE_LINE_COMMENT"+comment,new ConfigNode("__SINGLE_LINE_COMMENT"+comment,"__SINGLE_LINE_COMMENT"+comment,comment)); }
                        key="";
                        val="";
                        comment="";
                        STATUS = STATUS_KEY;
                        line++;
                        serialCode++;
                        //end line
                    } else if (c=='#'){
                        STATUS=STATUS_COMMENT;
                    } else if (c!='\r'){
                        val += c;
                    }
                    break;
                }
                case STATUS_COMMENT:
                {
                    if (c == '\n') {
                        if (comment.length()>1&&comment.charAt(comment.length()-1)=='\r'){
                            comment=comment.substring(0,comment.length()-1);
                        }
                        if (!val.equals("")||!key.equals("")) {
                            Object oVal = getVal(val);

                            nodes.put(key,new ConfigNode(key,oVal,comment));
                        }
                        //judge if is a single line comment
                        if (val.equals("") && key.equals("")) {
                            //save as a single line comment
                            nodes.put("__SINGLE_LINE_COMMENT"+comment,new ConfigNode("__SINGLE_LINE_COMMENT"+comment,"__SINGLE_LINE_COMMENT"+comment,comment));
                        }
                        key="";
                        val="";
                        comment="";
                        STATUS = STATUS_KEY;
                        line++;
                        serialCode++;
                        //end line
                    } else{
                        comment+=c;
                    }
                    break;
                }
            }
        }
        this.configNodes=nodes;
    }
    private Object getVal(String origin) throws IOException {
        //parse val line by line
        char[] chars=origin.toCharArray();
        boolean entered_text_block=false;
        boolean strict_string_rule=false;
        boolean may_be_end=false;
        int endPos=0;
        StringBuilder builder= new StringBuilder();
        for (char c :
                chars) {
            if (may_be_end)
                if (c!=' ')
                    may_be_end=false;
            if (!entered_text_block){
                if (c!=' '){
                    entered_text_block=true;
                    if (c=='\"'&&!strict_string_rule)
                        strict_string_rule=true;
                    else
                        builder.append(c);
                }
            } else {
                if (c=='\"'||c==' '){
                    may_be_end=true;
                    endPos=builder.length();//mark " here
                } else
                    builder.append(c);
            }
        }
        if (may_be_end)
            builder.delete(endPos,builder.length());
        origin=builder.toString();
        if (strict_string_rule){
            return origin;
        }
        if (origin.equals("true")) {
            return true;
        } else if (origin.equals("false")) {
            return false;
        } else if (StringUtils.isDigit(origin)) {
            if (origin.contains(".")) {
                return Float.parseFloat(origin);
            } else {
                BigInteger integer = new BigInteger(origin);
                if (integer.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1) {
                    exception(0, 0, "error parsing %s: integer too big".formatted(integer.toString()));
                } else if (integer.compareTo(BigInteger.valueOf(Integer.MAX_VALUE))==1){
                    return Long.parseLong(origin);
                } else {
                    return Integer.parseInt(origin);
                }
            }
        }
        return origin;
    }
}
