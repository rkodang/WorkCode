package com.gumdom.boot.util;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Common {

    private static short counter = (short) Math.abs(ThreadLocalRandom.current().nextInt());
    private static int JVM = (int) (System.currentTimeMillis() >>> 8);

    protected static int getJVM() {
        return JVM;
    }

    protected static short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    protected static int getLongTime() {
        return (int) System.currentTimeMillis();
    }

    protected static int getIP(){
        int ip;
        try {
            ip = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            ip = 0;
        }
        return ip;
    }

    private static int toInt(byte[] address) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8 ) - Byte.MIN_VALUE + address[i];
        }
        return result;
    }

    protected static int getCounter() {
        synchronized (Common.class){
            if(counter < 0){
                counter = 0;
            }

            if(counter >= Short.MAX_VALUE -1){
                synchronized (Common.class){
                    JVM = (int) (System.currentTimeMillis() >>> 8);
                }
            }
            return counter;
        }
    }

    protected static String format(int intValue) {
        String formattedInt = Integer.toHexString(intValue);
        StringBuilder builder = new StringBuilder("0000000");
        builder.replace(8 - formattedInt.length(), 8, formattedInt);
        return builder.toString();
    }

    protected static String format(short intValue) {
        String formattedInt = Integer.toHexString(intValue);
        StringBuilder builder = new StringBuilder("0000000");
        builder.replace(8 - formattedInt.length(), 8, formattedInt);
        return builder.toString();
    }

    /**
     * 生成有序的UUID
     */
    public static String nextUUId() {
        return nextUUId(true);
    }

    /**
     * 生成有序的UUID
     * noPlaceHolder 是否保留-占位符
     */
    public static String nextUUId(boolean noPlaceHolder) {
        StringBuilder builder = new StringBuilder(32);
        if (noPlaceHolder) {
            builder.append(format(getJVM()));
            builder.append(format(getHiTime()));
            builder.append(format(getLongTime()));
            builder.append(format(getIP()));
            builder.append(format(getCounter()));
            return builder.toString();
        }
        builder.append(format(getJVM()));
        builder.append("-");
        builder.append(format(getHiTime()));
        builder.append("-");
        builder.append(format(getLongTime()));
        builder.append("-");
        builder.append(format(getIP()));
        builder.append("-");
        builder.append(format(getCounter()));
        return builder.toString();
    }

    /**
     * UUID去掉字符"-"
     */
    public static String getUUId(){
       return UUID.randomUUID().toString().replace("-","");
    }


}
