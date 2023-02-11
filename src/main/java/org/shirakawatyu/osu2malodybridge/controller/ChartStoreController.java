package org.shirakawatyu.osu2malodybridge.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.osu2malodybridge.pojo.Chart;
import org.shirakawatyu.osu2malodybridge.pojo.DownloadList;
import org.shirakawatyu.osu2malodybridge.pojo.Song;
import org.shirakawatyu.osu2malodybridge.pojo.StoreList;
import org.shirakawatyu.osu2malodybridge.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口定义详见 <a href="https://gitlab.com/mugzone_team/malody_store_api/-/blob/main/README_CN.md">malody开发文档</a>
 * @author ShirakawaTyu
 */
@RestController
public class ChartStoreController {
    @Autowired
    StoreService storeService;
    @GetMapping("/api/store/list")
    public StoreList<Song> search(String word, Integer from, Integer beta, HttpSession session) {
        if (from == null) {
            from = 0;
        }
        if (beta == null) {
            beta = 0;
        }
        return storeService.search(word, from, beta, session);
    }

    @GetMapping("/api/store/promote")
    public StoreList<Song> promote(Integer from, HttpSession session) {
        if (from == null) {
            from = 0;
        }
        return storeService.promote(from, session);
    }

    @GetMapping("/api/store/query")
    public StoreList<Song> query(Integer sid, Integer cid,HttpSession session) {
        if (sid != null) {
            return storeService.search(Integer.toString(sid), 0, 1, session);
        } else {
            return storeService.search(Integer.toString(cid), 0, 1, session);
        }
    }

    @GetMapping("/api/store/charts")
    public StoreList<Chart> chartList(Integer sid, Integer from, HttpSession session) {
        if (from == null) {
            from = 0;
        }
        return storeService.chartList(sid, from, session);
    }

    @GetMapping("/api/store/download")
    public DownloadList download(int cid, HttpSession session) {
        return storeService.download(cid, session);
    }

    /**
     * 下载谱面的接口
     * @param type: 0: 准备就绪标志, 1: osu文件, 2: mp3文件, 3: jpg文件
     */
    @GetMapping("/api/store/d")
    public void down(@RequestParam("type") int type, HttpServletResponse response, HttpSession session) {
        storeService.sendChartFile(type, response, session);
    }
}
