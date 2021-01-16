package util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

public class HandleBytes {

    public static ArrayList<Byte> handleString(String str) {
        byte[] strInBytes = str.getBytes();
        ArrayList<Byte> res = new ArrayList<>();
        for(int i = 0; i < strInBytes.length; i++)
            res.add(strInBytes[i]);
        return res;
    }

    public static ArrayList<Byte> handleLong(Long num) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, num);
        ArrayList<Byte> res = new ArrayList<>();
        for(byte b : buffer.array()) {
            res.add(b);
        }
        return res;
    }

    public static ArrayList<Byte> handleDouble(double num) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(0, num);
        ArrayList<Byte> res = new ArrayList<>();
        for(byte b : buffer.array()) {
            res.add(b);
        }
        return res;
    }

    public static ArrayList<Byte> handleInt(int num) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0, num);
        ArrayList<Byte> res = new ArrayList<>();
        for(byte b : buffer.array()) {
            res.add(b);
        }
        return res;
    }

}
