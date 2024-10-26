package org.shirakawatyu.osu2malodybridge.util;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * 封装的发送请求
 * @author ShirakawaTyu
 * @date: 2022/10/1 17:45
 */

public class Requests {
    private static final Map<String, String> HEADERS = Map.of(
            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Encoding", "gzip, deflate, sdch",
            "Accept-Language", "zh-CN,zh;q=0.8",
            "Connection", "keep-alive",
            "Upgrade-Insecure-Requests", "1"
    );
    private static String token = "";


    /**
     * 本方法用于发起get请求，本质上是对RestTemplate的封装
     * @param url 要请求的URL
     * @param referer 来源，部分URL可能会验证referer
     * @param restTemplate 从外部传入一个restTemplate就行
     * @return 请求结果
     */
    public static ResponseEntity<String> get(String url, String referer, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(!"".equals(referer)) {
            headers.set("referer", referer);
        }
        if (!"".equals(token)) {
            headers.set("Authorization", "Bearer " + token);
        }
        HEADERS.forEach(headers::add);
        MultiValueMap<String, String> map1= new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> httpEntity1 = new HttpEntity<>(map1, headers);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity1, String.class);
    }

    /**
     * 本方法用于发起post请求，本质上是对RestTemplate的封装
     * @param url 要请求的URL
     * @param data 传参
     * @param restTemplate 从外部传入一个restTemplate就行
     * @return 请求结果
     */
    public static ResponseEntity<String> post(String url, MultiValueMap<String, String> data, String referer, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if(!"".equals(referer)) {
            headers.set("referer", referer);
        }
        if (!"".equals(token)) {
            headers.set("Authorization", "Bearer " + token);
        }
        HEADERS.forEach(headers::add);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(data, headers);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

    public static void setToken(String token) {
        Requests.token = token;
    }

    public static String getToken() {
        return Requests.token;
    }

}
