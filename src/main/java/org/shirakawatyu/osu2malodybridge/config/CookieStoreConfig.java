package org.shirakawatyu.osu2malodybridge.config;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieStoreConfig {
    @Bean
    public BasicCookieStore basicCookieStore() {
        return new BasicCookieStore();
    }
}
