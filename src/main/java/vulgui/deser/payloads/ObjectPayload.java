package vulgui.deser.payloads;


import com.sun.xml.internal.ws.util.StringUtils;


@SuppressWarnings("rawtypes")
public interface ObjectPayload<T> {

    /*
     * return armed payload object to be serialized that will execute specified
     * command on deserialization
     */
    public T getObject(Object template) throws Exception;

    public static class Utils {
        public static Class<? extends ObjectPayload> getPayloadClass(final String className) {
            Class<? extends ObjectPayload> clazz = null;
            try {
                clazz = (Class<? extends ObjectPayload>) Class.forName("vulgui.deser.payloads." + StringUtils.capitalize(className));
            } catch (Exception e1) {
            }
            return clazz;
        }
    }
}
