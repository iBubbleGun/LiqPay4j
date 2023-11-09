package com.github.ibubblegun.liqpay4j;

import java.util.Map;

public interface LiqPay4jApi {

    String API_VERSION = "3";
    String LIQPAY_API_URL = "https://www.liqpay.ua/api/";
    String LIQPAY_API_CHECKOUT_URL = "https://www.liqpay.ua/api/checkout";
    String DEFAULT_LANG = "ua";

    Map<String, Object> api(String path, Map<String, String> params) throws Exception;

    /**
     * Liq and Buy
     * Payment acceptance on the site client to server
     * To accept payments on your site you will need:
     * Register on www.liqpay.ua
     * Create a store in your account using install master
     * Get a ready HTML-button or create a simple HTML form
     * HTML form should be sent by POST to URL <a href="https://www.liqpay.ua/api/checkout">https://www.liqpay.ua/api/checkout</a>
     * Two parameters data and signature, where:
     * data - function result base64_encode( $json_string )
     * signature - function result base64_encode( sha1( $private_key . $data . $private_key ) )
     */
    String cnb_form(Map<String, String> params);
}
