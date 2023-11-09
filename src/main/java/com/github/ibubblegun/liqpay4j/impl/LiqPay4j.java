package com.github.ibubblegun.liqpay4j.impl;

import com.github.ibubblegun.liqpay4j.LiqPay4jApi;
import com.github.ibubblegun.liqpay4j.request.LiqPay4jRequest;
import com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.base64_encode;
import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.sha1;

public class LiqPay4j implements LiqPay4jApi {

    private final JSONParser parser = new JSONParser();
    private final String publicKey;
    private final String privateKey;
    private Proxy proxy;
    private String proxyLogin;
    private String proxyPassword;
    private boolean cnbSandbox;
    private boolean renderPayButton = true;
//    protected List<String> supportedCurrencies = Arrays.asList("EUR", "UAH", "USD", "RUB", "GEL");
//    protected List<String> supportedParams = Arrays.asList("public_key", "amount", "currency", "description", "order_id", "result_url", "server_url", "type", "signature", "language", "sandbox");

    public LiqPay4j(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        checkRequired();
    }

    public LiqPay4j(String publicKey, String privateKey, Proxy proxy, String proxyLogin, String proxyPassword) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.proxy = proxy;
        this.proxyLogin = proxyLogin;
        this.proxyPassword = proxyPassword;
        checkRequired();
    }

    private void checkRequired() {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new IllegalArgumentException("publicKey is empty!");
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("privateKey is empty!");
        }
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getProxyLogin() {
        return proxyLogin;
    }

    public void setProxyLogin(String proxyLogin) {
        this.proxyLogin = proxyLogin;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isCnbSandbox() {
        return cnbSandbox;
    }

    public void setCnbSandbox(boolean cnbSandbox) {
        this.cnbSandbox = cnbSandbox;
    }

    public boolean isRenderPayButton() {
        return renderPayButton;
    }

    public void setRenderPayButton(boolean renderPayButton) {
        this.renderPayButton = renderPayButton;
    }

    @Override
    public Map<String, Object> api(String path, Map<String, String> params) throws Exception {
        Map<String, String> data = generateData(params);
        String resp = LiqPay4jRequest.post(LIQPAY_API_URL + path, data, this.getProxyLogin(), this.getProxyPassword(), this.getProxy());
        JSONObject jsonObj = (JSONObject) parser.parse(resp);
        return LiqPay4jUtil.parseJson(jsonObj);
    }

    protected Map<String, String> generateData(Map<String, String> params) {
        HashMap<String, String> apiData = new HashMap<>();
        String data = base64_encode(JSONObject.toJSONString(withBasicApiParams(params)));
        apiData.put("data", data);
        apiData.put("signature", createSignature(data));
        return apiData;
    }

    protected TreeMap<String, String> withBasicApiParams(Map<String, String> params) {
        TreeMap<String, String> tm = new TreeMap<>(params);
        tm.put("public_key", publicKey);
        tm.put("version", API_VERSION);
        return tm;
    }

    protected TreeMap<String, String> withSandboxParam(@NotNull TreeMap<String, String> params) {
        if (params.get("sandbox") == null && isCnbSandbox()) {
            TreeMap<String, String> tm = new TreeMap<>(params);
            tm.put("sandbox", "1");
            return tm;
        }
        return params;
    }

    @Override
    public String cnb_form(Map<String, String> params) {
        checkCnbParams(params);
        String data = base64_encode(JSONObject.toJSONString(withSandboxParam(withBasicApiParams(params))));
        String signature = createSignature(data);
        String language = params.get("language") != null ? params.get("language") : DEFAULT_LANG;
        return renderHtmlForm(data, language, signature);
    }

    @Contract(pure = true)
    private @NotNull String renderHtmlForm(String data, String language, String signature) {
        String form = "";
        form += "<form method=\"post\" action=\"" + LIQPAY_API_CHECKOUT_URL + "\" accept-charset=\"utf-8\">\n";
        form += "<input type=\"hidden\" name=\"data\" value=\"" + data + "\" />\n";
        form += "<input type=\"hidden\" name=\"signature\" value=\"" + signature + "\" />\n";
        if (renderPayButton) {
            form += "<input type=\"image\" src=\"//static.liqpay.ua/buttons/p1" + language + ".radius.png\" name=\"btn_text\" />\n";
        }
        form += "</form>\n";
        return form;
    }

    protected void checkCnbParams(@NotNull Map<String, String> params) {
        if (params.get("amount") == null) {
            throw new NullPointerException("Amount can't be null!");
        }
        if (params.get("currency") == null) {
            throw new NullPointerException("Currency can't be null!");
        }
        if (params.get("description") == null) {
            throw new NullPointerException("Description can't be null!");
        }
    }

    protected String str_to_sign(String str) {
        return base64_encode(sha1(str));
    }

    protected String createSignature(String base64EncodedData) {
        return str_to_sign(privateKey
                .concat(base64EncodedData)
                .concat(privateKey)
        );
    }
}
