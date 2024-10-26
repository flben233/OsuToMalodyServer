package org.shirakawatyu.osu2malodybridge.config;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.osu2malodybridge.util.OsuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    OsuUtil osuUtil;
    @Autowired
    HttpSession session;
    @Value("${malody.server.tmp}")
    String tmpPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        osuUtil.autoLogin();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        File tmp = new File(tmpPath + File.separator + session.getId());
        if (tmp.exists() && tmp.listFiles().length == 0) {
            FileUtil.del(tmp);
        }
    }
}
