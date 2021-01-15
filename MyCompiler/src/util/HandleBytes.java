package util;

import java.util.ArrayList;

public class HandleBytes {

    public static ArrayList<Byte> handleString(String str) {
        byte[] strInBytes = str.getBytes();
        ArrayList<Byte> res = new ArrayList<>();
        for(int i = 0; i < strInBytes.length; i++)
            res.add(strInBytes[i]);
        return res;
    }

//    public static ArrayList<byte> handleLong(Long num) {
//        Long.toBinaryString(num);
//    }

}
