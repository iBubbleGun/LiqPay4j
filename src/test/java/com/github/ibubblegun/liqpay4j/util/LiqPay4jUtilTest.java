package com.github.ibubblegun.liqpay4j.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.base64_encode;
import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.getArray;
import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.parseJson;
import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.sha1;
import static org.junit.Assert.assertEquals;

public class LiqPay4jUtilTest {

    @Test
    public void testSha1() {
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", base64_encode(sha1("some string")));
    }

    @Test
    public void testBase64_encode() {
        assertEquals("c29tZSBzdHJpbmc=", base64_encode("some string".getBytes()));
    }

    @Test
    public void testBase64_encodeStr() {
        assertEquals("c29tZSBzdHJpbmc=", base64_encode("some string"));
    }

    @Test
    public void testGetArray() throws ParseException {
        // given:
        final JSONArray jsonArray = new JSONArray();
        jsonArray.add("string 1");
        jsonArray.add("string 2");

        final JSONArray innerJsonArray = new JSONArray();
        innerJsonArray.add("Inner string 1");
        innerJsonArray.add(1);
        innerJsonArray.add(1L);
        innerJsonArray.add(1.0F);
        innerJsonArray.add(1.0D);
        jsonArray.add(innerJsonArray);

        final Date someDate = new Date();
        jsonArray.add(someDate);

        final JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("stringField", "value");
        innerJsonObject.put("intField", 42);
        innerJsonObject.put("longField", Long.MAX_VALUE);
        innerJsonObject.put("doubleField", 33.3D);
        jsonArray.add(innerJsonObject);

        // when:
        final List parsedArray = getArray(jsonArray);
        // then:
        assertEquals("string 1", parsedArray.get(0));
        assertEquals("string 2", parsedArray.get(1));

        final JSONArray parsedInnerJsonArray = (JSONArray) parsedArray.get(2);
        assertEquals("Inner string 1", parsedInnerJsonArray.get(0));
        assertEquals(1, parsedInnerJsonArray.get(1));
        assertEquals(1L, parsedInnerJsonArray.get(2));
        assertEquals(1.0F, parsedInnerJsonArray.get(3));
        assertEquals(1.0D, parsedInnerJsonArray.get(4));
        assertEquals(someDate, parsedArray.get(3));

        final Map parsedInnerJsonObject = (Map) parsedArray.get(4);
        assertEquals("value", parsedInnerJsonObject.get("stringField"));
        assertEquals(42, parsedInnerJsonObject.get("intField"));
        assertEquals(Long.MAX_VALUE, parsedInnerJsonObject.get("longField"));
        assertEquals(33.3D, parsedInnerJsonObject.get("doubleField"));
    }

    @Test
    public void testParseJson() throws ParseException {
        // given:
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("stringField", "value");
        jsonObject.put("intField", 42);
        jsonObject.put("longField", Long.MAX_VALUE);
        jsonObject.put("doubleField", 33.3D);

        final JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("stringField", "value");
        jsonObject.put("innerJsonObject", innerJsonObject);

        final JSONArray jsonArray = new JSONArray();
        jsonArray.add("string 1");
        jsonArray.add("string 2");
        jsonObject.put("array", jsonArray);

        // when:
        final Map parsed = parseJson(jsonObject);
        // then:
        assertEquals("value", parsed.get("stringField"));
        assertEquals(42, parsed.get("intField"));
        assertEquals(Long.MAX_VALUE, parsed.get("longField"));
        assertEquals(33.3D, parsed.get("doubleField"));

        final Map parsedInnerJsonObject = (Map) parsed.get("innerJsonObject");
        assertEquals("value", parsedInnerJsonObject.get("stringField"));

        final List parsedArray = (List) parsed.get("array");
        assertEquals("string 1", parsedArray.get(0));
        assertEquals("string 2", parsedArray.get(1));
    }
}
