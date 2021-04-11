package vulgui.deser.frame;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * @className FramePayload
 * @Description 反序列化框架payload生成规范接口
 * @Author JF
 * @Date 2020/9/11 11:08
 * @Version 1.0
 **/
@SuppressWarnings("rawtypes")
public interface FramePayload<T> {

    /*
     * return armed payload object to be serialized that will execute specified
     * command on deserialization
     */
    public String sendpayload(Object ChainObject, String var) throws Exception;

    public String sendpayload(Object ChainObject) throws Exception;

    public static class Utils {
        public static Class<? extends FramePayload> getPayloadClass(final String className) {
            Class<? extends FramePayload> clazz = null;
            try {
                clazz = (Class<? extends FramePayload>) Class.forName("vulgui.deser.frame." + StringUtils.capitalize(className));
            } catch (Exception e1) {
            }
            return clazz;
        }
    }
}
