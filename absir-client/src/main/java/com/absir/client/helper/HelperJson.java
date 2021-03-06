/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.client.helper;

import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelString;
import com.absir.data.helper.HelperDataFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HelperJson {

    public static final ObjectMapper OBJECT_MAPPER = HelperDataFormat.JSON_MAPPER;

    public static String encode(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }

        ObjectWriter writer = OBJECT_MAPPER.writer();
        return writer.writeValueAsString(obj);
    }

    public static String encodeNull(Object obj) {
        try {
            return encode(obj);

        } catch (IOException e) {
            Environment.throwable(e);
            return null;
        }
    }

    public static <T> String encodeInclude(T obj, String... includes) throws IOException {
        SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("filter properties include",
                SimpleBeanPropertyFilter.serializeAllExcept(includes));
        ObjectWriter writer = OBJECT_MAPPER.writer(filterProvider);
        return writer.writeValueAsString(obj);
    }

    public static <T> String encodeExclude(T pojo, String... excludes) throws IOException {
        SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("filter properties include",
                SimpleBeanPropertyFilter.filterOutAllExcept(excludes));
        ObjectWriter writer = OBJECT_MAPPER.writer(filterProvider);
        return writer.writeValueAsString(pojo);
    }

    public static Object decode(String string) throws JsonProcessingException, IOException {
        ObjectReader reader = OBJECT_MAPPER.reader();
        return reader.readValue(string);
    }

    public static Object decodeNull(String string) {
        try {
            return decode(string);

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static <T> T decode(String string, Class toClass) throws JsonProcessingException, IOException {
        ObjectReader reader = OBJECT_MAPPER.reader(toClass);
        return reader.readValue(string);
    }

    public static <T> T decode(String string, Type toType) throws JsonProcessingException, IOException {
        ObjectReader reader = OBJECT_MAPPER.reader(OBJECT_MAPPER.constructType(toType));
        return reader.readValue(string);
    }

    public static <T> T decodeNull(String string, Class<T> toClass) {
        if (KernelString.isEmpty(string)) {
            return null;
        }

        try {
            return decode(string, toClass);

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static <T> T decodeNull(String string, Type toType) {
        if (string == null) {
            return null;
        }

        try {
            return decode(string, toType);

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static List decodeList(String string) {
        return decodeNull(string, List.class);
    }

    public static Map decodeMap(String string) {
        return decodeNull(string, Map.class);
    }

    public static byte[] encodeBase64(byte[] bytes) {
        return Base64.encodeBase64(bytes);
    }

    public static byte[] decodeBase64(byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    public static String encodeBase64String(byte[] bytes) {
        return new String(encodeBase64(bytes));
    }

    public static String decodeBase64String(byte[] bytes) {
        return new String(decodeBase64(bytes), KernelCharset.getDefault());
    }

    public static String encodeBase64String(String string) {
        return encodeBase64String(string.getBytes(KernelCharset.getDefault()));
    }

    public static String decodeBase64String(String string) {
        return decodeBase64String(string.getBytes());
    }

    public static String encodeBase64Json(Object obj) {
        try {
            return encodeBase64String(encode(obj));

        } catch (Exception e) {
            Environment.throwable(e);
            return null;
        }
    }

    public static <T> T decodeBase64Json(String string, Class<T> toClass) {
        if (KernelString.isEmpty(string)) {
            return null;
        }

        try {
            return decode(decodeBase64String(string), toClass);

        } catch (Exception e) {
            Environment.throwable(e);
            return null;
        }
    }

    public static <T> T decodeBase64Json(String string, Type toType) {
        if (string == null) {
            return null;
        }

        try {
            return decode(decodeBase64String(string), toType);

        } catch (Exception e) {
            Environment.throwable(e);
            return null;
        }
    }

    public static Map<String, Object> xmlToMap(String xml) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xml.getBytes(KernelCharset.getDefault()));
            SAXReader saxReader = new SAXReader();
            return xmlToMap(saxReader.read(inputStream).getRootElement());

        } catch (Exception e) {
            Environment.throwable(e);
            return null;

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (IOException e) {
                }
            }
        }
    }

    private static Map<String, Object> xmlToMap(Element element) {
        Iterator<Element> iterator = element.elementIterator();
        if (iterator.hasNext()) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            while (true) {
                Element ele = iterator.next();
                Map<String, Object> eleMap = xmlToMap(ele);
                map.put(ele.getName(), eleMap == null ? ele.getText() : eleMap);
                if (!iterator.hasNext()) {
                    break;
                }
            }

            return map;
        }

        return null;
    }

    public static String mapToXml(Map<String, Object> map, String rootName) {
        return mapToXml(map, rootName, null);
    }

    public static String mapToXml(Map<String, Object> map, String rootName, String data) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mapToXmlWriter(outputStream, map, rootName);
            if (data != null) {
                // 预留参数
            }

            return new String(outputStream.toByteArray(), KernelCharset.UTF8);

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static XMLWriter mapToXmlWriter(OutputStream outputStream, Map<String, Object> map, String rootName)
            throws IOException {
        XMLWriter xmlWriter = new XMLWriter(outputStream);
        Document document = DocumentHelper.createDocument();
        if (rootName == null) {
            rootName = "xml";
        }

        Element element = document.addElement(rootName);
        mapToXmlWriter(map, element);
        xmlWriter.write(document);
        return xmlWriter;
    }

    protected static void mapToXmlWriter(Map<String, Object> map, Element element) {
        for (Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof Map) {
                    mapToXmlWriter((Map<String, Object>) value, element.addElement(entry.getKey()));

                } else {
                    element.addAttribute(entry.getKey(), value.toString());
                }
            }
        }
    }

    public static String mapToXmlBuilder(Map<String, Object> map, String rootName) {
        StringBuilder stringBuilder = new StringBuilder();
        mapToXmlBuilder(stringBuilder, map, rootName);
        return stringBuilder.toString();
    }

    public static void mapToXmlBuilder(StringBuilder stringBuilder, Map<String, Object> map, String rootName) {
        if (rootName == null) {
            rootName = "xml";
        }

        stringBuilder.append('<' + rootName + '>');
        mapToXmlBuilder(1, stringBuilder, map);
        stringBuilder.append("\r\n");
        stringBuilder.append("</" + rootName + '>');
    }

    public static void mapToXmlBuilder(int index, StringBuilder stringBuilder, Map<String, Object> map) {
        for (Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value != null) {
                stringBuilder.append("\r\n");
                for (int i = 0; i < index; i++) {
                    stringBuilder.append("  ");
                }

                stringBuilder.append('<' + key + '>');
                if (value instanceof Map) {
                    mapToXmlBuilder(index + 1, stringBuilder, (Map<String, Object>) value);
                    stringBuilder.append("\r\n");
                    for (int i = 0; i < index; i++) {
                        stringBuilder.append("  ");
                    }

                } else {
                    stringBuilder.append(value);
                }

                stringBuilder.append("</" + key + '>');
            }
        }
    }

    public static Map<String, String> buildParams(Map<String, ?> map) {
        Map<String, String> params = new HashMap<String, String>();
        for (Entry<String, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value instanceof Object[]) {
                value = ((Object[]) value)[0];
            }

            params.put(entry.getKey(), DynaBinder.to(value, String.class));
        }

        return params;
    }

    public static StringBuilder builderSign(Map<String, ?> map, Object signKey, String[] filterKeys) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Entry<String, ?> entry : map.entrySet()) {
            if (filterKeys != null && KernelArray.contains(filterKeys, entry.getKey())) {
                continue;
            }

            if (first) {
                first = false;

            } else {
                stringBuilder.append('&');
            }

            stringBuilder.append(entry.getKey());
            stringBuilder.append('=');
            stringBuilder.append(entry.getValue());
        }

        if (signKey != null) {
            stringBuilder.append(signKey);
        }

        return stringBuilder;
    }

    public static StringBuilder builderSignSort(Map<String, ?> map, Object signKey, String[] filterKeys) {
        if (map instanceof TreeMap) {
            return builderSign(map, signKey, filterKeys);
        }

        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String key : keys) {
            if (filterKeys != null && KernelArray.contains(filterKeys, key)) {
                continue;
            }

            if (first) {
                first = false;

            } else {
                stringBuilder.append('&');
            }

            stringBuilder.append(key);
            stringBuilder.append('=');
            stringBuilder.append(map.get(key));
        }

        if (signKey != null) {
            stringBuilder.append(signKey);
        }

        return stringBuilder;
    }

    public static StringBuilder builderSignSortValues(Map<String, ?> map, String[] filterKeys) {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keys) {
            if (filterKeys != null && KernelArray.contains(filterKeys, key)) {
                continue;
            }

            stringBuilder.append(map.get(key));
        }

        return stringBuilder;
    }
}
