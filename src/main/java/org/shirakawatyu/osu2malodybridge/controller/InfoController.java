package org.shirakawatyu.osu2malodybridge.controller;

import org.shirakawatyu.osu2malodybridge.pojo.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 接口定义详见 <a href="https://gitlab.com/mugzone_team/malody_store_api/-/blob/main/README_CN.md">...</a>
 * @author ShirakawaTyu
 */
@Controller
public class InfoController {
    @Autowired
    ServerInfo serverInfo;
    @GetMapping("/api/store/info")
    @ResponseBody
    public ServerInfo getBasicInfo() {
        return serverInfo;
    }
}
