package vulgui.test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * @className MyClass
 * @Description TODO
 * @Author sunnylast0
 * @Date 2020/11/7 10:00
 * @Version 1.0
 **/
public class MyClass {
    public static String toHexString(String input) {
        return String.format("%x", new BigInteger(1, input.getBytes()));
    }

    public static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }

    public static void main(String args[]) throws Exception {
        String a = "wadawdawd wadawdawd 123";
        String hexString = MyClass.toHexString(a);
        System.out.println(hexString);

        String orgin_s = MyClass.fromHexString(hexString);
        System.out.println(orgin_s);
    }
}
