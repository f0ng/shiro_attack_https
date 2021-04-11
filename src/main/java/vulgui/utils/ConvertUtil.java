package vulgui.utils;

/**
 * @className ConvertUtil
 * @Description TODO
 * @Author sunnylast0
 * @Date 2020/11/7 10:42
 * @Version 1.0
 **/
public class ConvertUtil {
    public static String toHexString(String input) {
        return String.format("%x", new java.math.BigInteger(1, input.getBytes()));
    }

    public static String fromHexString(String hex) {
        StringBuilder str = new java.lang.StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }
}
