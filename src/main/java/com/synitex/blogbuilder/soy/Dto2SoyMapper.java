package com.synitex.blogbuilder.soy;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class Dto2SoyMapper implements IDto2SoyMapper {

    private static Set<Class<?>> primitiveTypes = new HashSet<>();
    static {
        primitiveTypes.add(String.class);
        primitiveTypes.add(Integer.class);
        primitiveTypes.add(Integer.TYPE);
        primitiveTypes.add(Long.class);
        primitiveTypes.add(Long.TYPE);
        primitiveTypes.add(Boolean.class);
        primitiveTypes.add(Boolean.TYPE);
    }
    
    @Override
    public SoyMapData map(Object obj) {
        return mapImpl(obj);
    }

    @Override
    public <T> SoyListData mapList(List<T> objects) {
        return buildSoyList(objects, new Function<T, SoyMapData>() {
            @Override
            public SoyMapData apply(T value) {
                return mapImpl(value);
            }
        });
    }

    private SoyMapData mapImpl(final Object data) {
        final SoyMapData map = new SoyMapData();
        ReflectionUtils.doWithFields(data.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                Object obj = field.get(data);
                if (obj == null) {
                    return;
                }

                String id = field.getName();
                Class<?> type = field.getType();
                Type gtype = field.getGenericType();

                if (isListType(type, gtype)) {

                    Class<?> itemType = resolveClassOfListItem(gtype);
                    if (primitiveTypes.contains(itemType)) {
                        map.put(id, obj);
                    } else {
                        map.put(id, buildSoyList((List) obj, new Function<Object, SoyMapData>() {
                            @Override
                            public SoyMapData apply(Object value) {
                                SoyMapData mapData = mapImpl(value);
                                return mapData;
                            }
                        }));
                    }

                } else if (primitiveTypes.contains(type)) {

                    map.put(id, obj);

                } else {

                    map.put(id, mapImpl(obj));

                }
            }
        });

        return map;
    }

    private <IN> SoyListData buildSoyList(List<IN> list, Function<IN, SoyMapData> function) {
        if (Iterables.isEmpty(list)) {
            return new SoyListData();
        }
        SoyListData data = new SoyListData();
        for (IN t : list) {
            SoyMapData r = function.apply(t);
            if (r != null) {
                data.add(r);
            }
        }
        return data;
    }

    private boolean isListType(Class<?> clazz, Type gtype) {
        return List.class.isAssignableFrom(clazz) && ParameterizedType.class.isAssignableFrom(gtype.getClass());
    }

    private Class<?> resolveClassOfListItem(Type gtype) {
        ParameterizedType pt = (ParameterizedType) gtype;
        Type[] actualTypes = pt.getActualTypeArguments();
        Class<?> itemType = (Class<?>) actualTypes[0];
        return itemType;
    }

}
