package vulgui.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 提供通过HTTP协议获取内容的方法 <br/>
 * 所有提供方法中的params参数在内部不会进行自动的url encode，如果提交参数需要进行url encode，请调用方自行处理
 *
 * @author lushuifa
 * @Description: HTTP请求代理工具
 * @date 2016年11月18日 上午11:21:05
 */
public class HttpUtil {
    /**
     * logger
     */

    /**
     * 支持的Http method
     */
    private static enum HttpMethod {
        POST, DELETE, GET, PUT, HEAD;
    }

    // 修改的
    static {
        try {
            HttpUtil.disableSSLCertificateChecking();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    // 修改的截止

    public static Map<String, List<String>> getRespHeader(HttpURLConnection conn) {
        return conn.getHeaderFields();
    }

    public static String getBody(HttpURLConnection conn, String encoding) {
        //接收返回结果
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

            int BUFFER_SIZE = 1024;
            char[] buffer = new char[BUFFER_SIZE]; // or some other size,
            int charsRead = 0;
            while ((charsRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                result.append(buffer, 0, charsRead);
            }
        } catch (Exception e) {
            // 忽略EOF错误
            String error = e.getMessage();
            if (!"Premature EOF".contains(error)) {
                System.out.println(error);
            }
            //处理错误流，提高http连接被重用的几率
            try {
                byte[] buf = new byte[100];
                InputStream es = conn.getErrorStream();
                if (es != null) {
                    while (es.read(buf) > 0) {
                        ;
                    }
                    es.close();
                }
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        }
        if (!"".equals(result.toString())) {
            return result.toString();
        } else {
            return null;
        }
    }


// 修改的
    // https://gist.github.com/aembleton/889392
    private static void disableSSLCertificateChecking() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier(){
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }

        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        URL url = new URL(https_url);
//        HttpsURLConnection  conn = (HttpsURLConnection)url.openConnection();
//        SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
//        conn.setSSLSocketFactory(sslSocketFactory);
    }

// 修改的截止


    
    private static HttpURLConnection invokeUrl(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String encoding, HttpMethod method) {
        headers.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
        //构造请求参数字符串
        StringBuilder paramsStr = null;
        if (params != null) {
            paramsStr = new StringBuilder();
            Set<Map.Entry> entries = params.entrySet();
            for (Map.Entry entry : entries) {
                String value = (entry.getValue() != null) ? (String.valueOf(entry.getValue())) : "";
                paramsStr.append(entry.getKey() + "=" + value + "&");
            }
            //只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                url += "?" + paramsStr.toString();
            }
        }

        URL uUrl = null;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        //接收返回结果
        StringBuilder result = new StringBuilder();

        try {
            //创建和初始化连接
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            // conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod(method.toString());
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            //设置读取超时时间
            conn.setReadTimeout(readTimeout);
            //指定请求header参数
            if (headers != null && headers.size() > 0) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            if (paramsStr != null && method == HttpMethod.POST) {
                //发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
                out.write(paramsStr.toString());
                out.flush();
            }

            return conn;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //关闭连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
/*
    private static String invokeUrl(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String encoding, HttpMethod method) {
        headers.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
        //构造请求参数字符串
        StringBuilder paramsStr = null;
        if (params != null) {
            paramsStr = new StringBuilder();
            Set<Map.Entry> entries = params.entrySet();
            for (Map.Entry entry : entries) {
                String value = (entry.getValue() != null) ? (String.valueOf(entry.getValue())) : "";
                paramsStr.append(entry.getKey() + "=" + value + "&");
            }
            //只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                url += "?" + paramsStr.toString();
            }
        }

        URL uUrl = null;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        //接收返回结果
        StringBuilder result = new StringBuilder();

        try {
            //创建和初始化连接
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod(method.toString());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            //设置读取超时时间
            conn.setReadTimeout(readTimeout);
            //指定请求header参数
            if (headers != null && headers.size() > 0) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            if (paramsStr != null && method == HttpMethod.POST) {
                //发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
                out.write(paramsStr.toString());
                out.flush();
            }

            // 判断teste回显echo返回头是否存在
            if (headers.containsKey("Teste")) {
                Map<String, List<String>> resheaders = conn.getHeaderFields();
                if (resheaders.containsKey("Teste")) {
                    return "1";
                } else {
                    return null;
                }
            }

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

            int BUFFER_SIZE = 1024;
            char[] buffer = new char[BUFFER_SIZE]; // or some other size,
            int charsRead = 0;
            while ((charsRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                result.append(buffer, 0, charsRead);
            }
        } catch (Exception e) {

            // 忽略EOF错误
            String error = e.getMessage();
            if (!"Premature EOF".contains(error)) {
                System.out.println(error);
            }
            //处理错误流，提高http连接被重用的几率
            try {
                byte[] buf = new byte[100];
                InputStream es = conn.getErrorStream();
                if (es != null) {
                    while (es.read(buf) > 0) {
                        ;
                    }
                    es.close();
                }
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //关闭连接
            if (conn != null) {
                conn.disconnect();
            }
        }


        if (!"".equals(result.toString())) {
            return result.toString();
        } else {
            return null;
        }

    }
*/

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection post(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection post(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection get(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection get(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection put(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection put(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection delete(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection delete(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法  <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection head(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法  <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     *
     * @param url            资源路径（如果url中已经包含参数，则params应该为null）
     * @param params         参数
     * @param headers        请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout    读取超时时间（单位为ms）
     * @param charset        字符集（一般该为“utf-8”）
     * @return
     */
    public static HttpURLConnection head(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }

    public static void main(String[] args) {
        //Map params = new HashMap();
        //params.put("phoneNo", "中文");
        //String str = HttpUtil.get("http://localhost:9092/elis_smp_als_dmz/do/app/activitySupport/demo", params, 3000, 3000, "UTF-8");
        //System.out.println(str);
    }

}
