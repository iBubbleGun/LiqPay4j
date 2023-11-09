package com.github.ibubblegun.liqpay4j.util;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class LiqPay4jUtil {

    public static byte[] sha1(String param) {
        try {
            MessageDigest SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes(StandardCharsets.UTF_8));
            return SHA.digest();
        } catch (Exception e) {
            throw new RuntimeException("Can't calculate SHA-1 hash.", e);
        }
    }

    public static String base64_encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static String base64_encode(@NotNull String data) {
        return base64_encode(data.getBytes());
    }

    public static @NotNull ArrayList<Object> getArray(Object object2) throws ParseException {
        ArrayList<Object> list = new ArrayList<>();
        JSONArray jsonArr = (JSONArray) object2;
        for (Object aJsonArr : jsonArr) {
            if (aJsonArr instanceof JSONObject) {
                list.add(parseJson((JSONObject) aJsonArr));
            } else {
                list.add(aJsonArr);
            }
        }
        return list;
    }

    public static @NotNull HashMap<String, Object> parseJson(@NotNull JSONObject jsonObject) throws ParseException {
        HashMap<String, Object> data = new HashMap<>();
        for (Object obj : jsonObject.keySet()) {
            if (jsonObject.get(obj) instanceof JSONArray) {
                data.put(obj.toString(), getArray(jsonObject.get(obj)));
            } else {
                if (jsonObject.get(obj) instanceof JSONObject) {
                    data.put(obj.toString(), parseJson((JSONObject) jsonObject.get(obj)));
                } else {
                    data.put(obj.toString(), jsonObject.get(obj));
                }
            }
        }
        return data;
    }
}
