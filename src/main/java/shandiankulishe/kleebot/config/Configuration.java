package shandiankulishe.kleebot.config;

import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private static final int STATUS_KEY=0;
    private static final int STATUS_VALUE=1;
    private static final int STATUS_COMMENT=2;
    private File configFile;
    private String configContent;
    private Map<String,ConfigValue> configMap;
    public Configuration(){
    }
    public Configuration(File file){
        this.configFile=file;
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
    public Map<String,ConfigValue> getConfigMap(){
        return this.configMap;
    }
    public <T> T serializeToClass(Class clz){
        try {
            Object obj=clz.getConstructors()[0].newInstance();
            Field[] fields=clz.getDeclaredFields();
            for (Field f :
                    fields) {
                f.setAccessible(true);
                Object val;
                if ((val=getObject(f.getName()))!=null) {
                    f.set(obj,val);
                }
            }
            return (T) obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void mergeClass(Object obj){
        Field[] fields=obj.getClass().getDeclaredFields();
        for (Field f :
                fields) {
            f.setAccessible(true);
            try {
                setValue(f.getName(),f.get(obj),configMap.get(f.getName()).getComment());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    public String save(){
        StringBuilder builder=new StringBuilder();
        for (Map.Entry<String, ConfigValue> entry :
                configMap.entrySet()) {
            builder.append(entry.getKey()).append(" : ").append(entry.getValue().getVal()).append(" #").append(entry.getValue().getComment()).append("\n");
        }
        return builder.toString();
    }
    public void saveToFile() throws IOException {
        if (configFile!=null){
            FileUtils.writeFile(configFile.getAbsolutePath(),save());
        } else{
            exception(0,0,"saveToFile() can be only used in load(java.io.File) method.");
        }
    }
    public void setValue(String key,ConfigValue value){
        this.configMap.put(key,value);
    }
    public void setValue(String key,Object value,String comment){
        this.setValue(key,new ConfigValue(value,comment));
    }
    public void setValue(String key,Object value){
        this.setValue(key,value,"");
    }
    public <T> T get(String key) throws IOException {
        return (T)getObject(key);
    }
    public String getComment(String key) throws IOException {
        ConfigValue obj=this.configMap.get(key);
        if (obj==null){
            exception(0,0,"%s not found".formatted(key));
        }
        return obj.getComment();
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
    private Object getObject(String key) throws IOException {
        Object obj=this.configMap.get(key).getVal();;
        if (obj==null){
            exception(0,0,"%s not found".formatted(key));
        }
        return obj;
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
        Map<String,ConfigValue> objects=new LinkedHashMap<>();
        String key="";
        String val="";
        String comment="";
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
                        if (!val.equals("")||!key.equals("")) {
                            Object oVal = getVal(val);
                            objects.put(key, new ConfigValue(oVal, comment));
                        }
                        key="";
                        val="";
                        comment="";
                        STATUS = STATUS_KEY;
                        line++;
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
                        if (!val.equals("")){
                            Object oVal=getVal(val);
                            objects.put(key,new ConfigValue(oVal,comment));
                        }
                        key="";
                        val="";
                        comment="";
                        STATUS=STATUS_KEY;
                        line++;
                        //end line
                    } else{
                        comment+=c;
                    }
                    break;
                }
            }
        }
        this.configMap=objects;
    }
    private Object getVal(String origin) throws IOException {
        if (origin.startsWith(" ")){
            origin=origin.substring(1);
        }
        if (origin.endsWith(" ")){
            origin=origin.substring(0,origin.length()-1);
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
