package vulgui;

import vulgui.exp.DserUtil;
import vulgui.utils.HttpUtil;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test2 {
    public static void main(String[] args) {

        //在你发起Http请求之前设置一下属性

        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "8080");

        Map<String, String> headers = new HashMap<String, String>();
        String targeturl = "https://xxxx";
        int timeout = 100;
        headers.put("Cookie", "rememberMe=1");
        HttpURLConnection conn = HttpUtil.get(targeturl, null, headers, timeout, timeout, "utf-8");
        conn.setChunkedStreamingMode(0);
        Map<String, List<String>> resheaders = conn.getHeaderFields();
        System.out.println(resheaders);
        if (resheaders.containsKey("Set-Cookie")) {
            List<String> list = resheaders.get("Set-Cookie");
            for (String item : list) {
                if (item.contains("rememberMe")) {
                    System.out.println("false");
                }
            }
        }
        conn.disconnect();

    }
}
