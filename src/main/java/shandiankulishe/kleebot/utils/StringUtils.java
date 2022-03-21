package shandiankulishe.kleebot.utils;

import java.util.Vector;

public class StringUtils {
    public static boolean isDigit(String str){
        if (str==null){
            return false;
        }
        for (char c:str.toCharArray()){
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }
    public static String findDigit(String str){
        if (str==null){
            return null;
        }
        boolean haveDigit=false;
        Vector<Character> charvct=new Vector<>();
        for (char c:str.toCharArray()){
            if (Character.isDigit(c)){
                haveDigit=true;
                charvct.add(c);
            } else{
                if (haveDigit){
                    break;
                }
            }
        }
        Character[] outChars=charvct.toArray(new Character[charvct.size()]);
        String outStr ="";
        for (char c :
                outChars) {
            outStr+=c;
        }
        return outStr;
    }
}
