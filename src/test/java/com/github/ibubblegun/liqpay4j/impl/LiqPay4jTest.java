package com.github.ibubblegun.liqpay4j.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.base64_encode;
import static org.junit.Assert.assertEquals;

public class LiqPay4jTest {

    static final String CNB_FORM_WITHOUT_SANDBOX = """
            <form method="post" action="https://www.liqpay.ua/api/checkout" accept-charset="utf-8">
            <input type="hidden" name="data" value="eyJhbW91bnQiOiIxLjUiLCJjdXJyZW5jeSI6IlVTRCIsImRlc2NyaXB0aW9uIjoiRGVzY3JpcHRpb24iLCJsYW5ndWFnZSI6ImVuIiwicHVibGljX2tleSI6InB1YmxpY0tleSIsInZlcnNpb24iOiIzIn0=" />
            <input type="hidden" name="signature" value="krCwuK4CBtNFAb6zqmJCeR/85VU=" />
            <input type="image" src="//static.liqpay.ua/buttons/p1en.radius.png" name="btn_text" />
            </form>
            """;

    static final String CNB_FORM_WITH_SANDBOX = """
            <form method="post" action="https://www.liqpay.ua/api/checkout" accept-charset="utf-8">
            <input type="hidden" name="data" value="eyJhbW91bnQiOiIxLjUiLCJjdXJyZW5jeSI6IlVTRCIsImRlc2NyaXB0aW9uIjoiRGVzY3JpcHRpb24iLCJsYW5ndWFnZSI6ImVuIiwicHVibGljX2tleSI6InB1YmxpY0tleSIsInNhbmRib3giOiIxIiwidmVyc2lvbiI6IjMifQ==" />
            <input type="hidden" name="signature" value="jDmdwKnagO2JhE1ONHdk3F7FG0c=" />
            <input type="image" src="//static.liqpay.ua/buttons/p1en.radius.png" name="btn_text" />
            </form>
            """;

    private LiqPay4j lp;

    @Before
    public void setUp() {
        lp = new LiqPay4j("publicKey", "privateKey");
    }

    @Test
    public void testCnbFormWithoutSandboxParam() {
        final Map<String, String> params = defaultTestParams("sandbox");
        assertEquals(CNB_FORM_WITHOUT_SANDBOX, lp.cnb_form(params));
    }

    @Test
    public void testCnbFormWithSandboxParam() {
        final Map<String, String> params = defaultTestParams(null);
        assertEquals(CNB_FORM_WITH_SANDBOX, lp.cnb_form(params));
    }

    @Test
    public void testCnbFormWillSetSandboxParamIfItEnabledGlobally() {
        final Map<String, String> params = defaultTestParams("sandbox");
        lp.setCnbSandbox(true);
        assertEquals(CNB_FORM_WITH_SANDBOX, lp.cnb_form(params));
    }

    private @NotNull @UnmodifiableView Map<String, String> defaultTestParams(String removedKey) {
        final Map<String, String> params = new TreeMap<>();
        params.put("language", "en");
        params.put("amount", "1.5");
        params.put("currency", "USD");
        params.put("description", "Description");
        params.put("sandbox", "1");
        if (removedKey != null) {
            params.remove(removedKey);
        }
        return Collections.unmodifiableMap(params);
    }

    @Test
    public void testCnbParams() {
        final Map<String, String> cnbParams = defaultTestParams(null);
        lp.checkCnbParams(cnbParams);
        assertEquals("en", cnbParams.get("language"));
        assertEquals("USD", cnbParams.get("currency"));
        assertEquals("1.5", cnbParams.get("amount"));
        assertEquals("Description", cnbParams.get("description"));
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotAmount() {
        final Map<String, String> params = defaultTestParams("amount");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotCurrency() {
        final Map<String, String> params = defaultTestParams("currency");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotDescription() {
        final Map<String, String> params = defaultTestParams("description");
        lp.checkCnbParams(params);
    }

    @Test
    public void testWithBasicApiParams() {
        final Map<String, String> cnbParams = defaultTestParams(null);
        final Map<String, String> fullParams = lp.withBasicApiParams(cnbParams);
        assertEquals("publicKey", fullParams.get("public_key"));
        assertEquals("3", fullParams.get("version"));
        assertEquals("1.5", fullParams.get("amount"));
    }

    @Test
    public void testStrToSign() {
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", lp.str_to_sign("some string"));
    }

    @Test
    public void testCreateSignature() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("field", "value");
        String base64EncodedData = base64_encode(jsonObject.toString());
        assertEquals("d3dP/5qWQFlZgFR53eAwqJ+xIOQ=", lp.createSignature(base64EncodedData));
    }

    @Test
    public void testGenerateData() {
        final Map<String, String> invoiceParams = new TreeMap<>();
        invoiceParams.put("email", "client-email@gmail.com");
        invoiceParams.put("amount", "200");
        invoiceParams.put("currency", "USD");
        invoiceParams.put("order_id", "order_id_1");
        invoiceParams.put("goods", "[{amount: 100, count: 2, unit: 'un.', name: 'phone'}]");

        final Map<String, String> generated = lp.generateData(Collections.unmodifiableMap(invoiceParams));
        assertEquals("DqcGjvo2aXgt0+zBZECdH4cbPWY=", generated.get("signature"));
        assertEquals("eyJhbW91bnQiOiIyMDAiLCJjdXJyZW5jeSI6IlVTRCIsImVtYWlsIjoiY2xpZW50LWVtYWlsQGdtYWlsLmNvbSIsImdvb2RzIjoiW3thbW91bnQ6IDEwMCwgY291bnQ6IDIsIHVuaXQ6ICd1bi4nLCBuYW1lOiAncGhvbmUnfV0iLCJvcmRlcl9pZCI6Im9yZGVyX2lkXzEiLCJwdWJsaWNfa2V5IjoicHVibGljS2V5IiwidmVyc2lvbiI6IjMifQ==", generated.get("data"));
    }
}
