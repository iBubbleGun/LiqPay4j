package com.github.ibubblegun.liqpay4j.request;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LiqPay4jRequestTest {

    @Test
    public void testGetProxyUser() {
        assertEquals("dXNlcjpwYXNz", LiqPay4jRequest.getProxyUser("user", "pass"));
    }
}
