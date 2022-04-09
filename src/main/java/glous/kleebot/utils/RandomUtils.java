package glous.kleebot.utils;

import java.util.*;

public class RandomUtils {
    private static final String RANDOM_CHAR_LIST="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random random=new Random();
    public static char newRandomUpperCaseString(){
        return (char)(random.nextInt(26)+'A');
    }
    public static char newRandomLowerCaseString(){
        return (char)(random.nextInt(26)+'a');
    }
    public static char newRandomNumber(){
        return (char)(random.nextInt(10)+'0');
    }
    public static String newRandomNumberSeries(int length){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(newRandomNumber());
        }
        return builder.toString();
    }
    public static String newRandomUpperCaseCharSeries(int length){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(newRandomUpperCaseString());
        }
        return builder.toString();
    }
    public static String newRandomLowerCaseCharSeries(int length){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(newRandomLowerCaseString());
        }
        return builder.toString();
    }
    public static String newRandomMixedString(int length){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(RANDOM_CHAR_LIST.charAt(random.nextInt(62)));
        }
        return builder.toString();
    }
}
