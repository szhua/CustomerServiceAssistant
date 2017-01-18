package com.pcjh.assistant.util;

/**
 * CustomerServiceAssistant
 * Create   2017/1/11 14:47;
 * https://github.com/szhua
 * @author sz.hua
 */
public  final class IntToLongUtil {

    public static String getUinString(long original){
        int j=(int)original;
        //这里输出的是-1062731417
        //这里你用右位移运算，向右位移一位
        long temp=j>>>1;
        temp<<=1;
        //判断是否为奇数，奇数加1
        if(j%2!=0){
            temp+=1;
        }
        return  String.valueOf(temp);
    }
}
