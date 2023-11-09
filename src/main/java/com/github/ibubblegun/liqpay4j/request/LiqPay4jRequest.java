package com.github.ibubblegun.liqpay4j.request;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.ibubblegun.liqpay4j.util.LiqPay4jUtil.base64_encode;

public class LiqPay4jRequest {

    public static @NotNull String post(String url, @NotNull Map<String, String> list, String proxyLogin, String proxyPassword, Proxy proxy) throws IOException {

        String urlParameters = list.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8) + "&")
                .collect(Collectors.joining());

        final URL obj = URI.create(url).toURL();

        HttpURLConnection con;
        if (proxy == null) {
            con = (HttpURLConnection) obj.openConnection();
        } else {
            con = (HttpURLConnection) obj.openConnection(proxy);
            if (proxyLogin != null) {
                con.setRequestProperty("Proxy-Authorization", "Basic " + getProxyUser(proxyLogin, proxyPassword));
            }
        }
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
            out.writeBytes(urlParameters);
            out.flush();
        }

        String response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            response = in.lines().collect(Collectors.joining());
        }
        return response;
    }

    public static String getProxyUser(@NotNull String proxyLogin, String proxyPassword) {
        return base64_encode(proxyLogin
                .concat(":")
                .concat(proxyPassword)
        );
    }
}
