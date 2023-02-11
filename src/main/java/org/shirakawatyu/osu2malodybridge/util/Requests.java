package org.shirakawatyu.osu2malodybridge.util;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @description: 封装的发送请求
 * @author ShirakawaTyu
 * @date: 2022/10/1 17:45
 */

public class Requests {
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
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(data, headers);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }

}
