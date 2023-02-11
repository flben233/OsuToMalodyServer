package org.shirakawatyu.osu2malodybridge.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
public class ServerInfo {
    int code;
    @Value("${malody.server.api}")
    int api;
    @Value("${malody.server.min}")
    int min;
    @Value("${malody.server.welcome}")
    String welcome;

    public ServerInfo() {
        this.code = 0;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "code=" + code +
                ", api=" + api +
                ", min=" + min +
                ", welcome='" + welcome + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getApi() {
        return api;
    }

    public void setApi(int api) {
        this.api = api;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }
}
