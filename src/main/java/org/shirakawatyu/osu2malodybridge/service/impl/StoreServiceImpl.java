package org.shirakawatyu.osu2malodybridge.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.compress.extractor.StreamExtractor;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.HttpHeaders;
import org.shirakawatyu.osu2malodybridge.pojo.*;
import org.shirakawatyu.osu2malodybridge.service.DownloadService;
import org.shirakawatyu.osu2malodybridge.service.StoreService;
import org.shirakawatyu.osu2malodybridge.util.OsuUtil;
import org.shirakawatyu.osu2malodybridge.util.Requests;
import org.shirakawatyu.osu2malodybridge.util.StringUtil;
import org.shirakawatyu.osu2malodybridge.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    DownloadService downloadService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OsuUtil osuUtil;
    @Autowired
    HttpProxy httpProxy;
    @Value("${malody.server.tmp}")
    String tmpPath;
    @Value("${malody.server.url}")
    String serverUrl;
    @Value("${malody.server.saveTemp}")
    boolean saveTemp;
    Logger log = Logger.getLogger("o.s.o.s.i.StoreServiceImpl");
    @Override
    public StoreList<Song> search(String word, int from, int beta, HttpSession session) {
        JSONArray beatMapSets;
        if (word == null || word.length() == 0) {
            beatMapSets = osuUtil.osuSearch("3", "", "", "", "spotlights", session);
        } else if (beta == 0) {
            beatMapSets = osuUtil.osuSearch("3", "", word, session);
        } else {
            beatMapSets = osuUtil.osuSearch("3", "any", "", word, "", session);
        }
        return osuUtil.osu2Malody(from, beatMapSets, session);
    }

    @Override
    public StoreList<Song> promote(int from, HttpSession session) {
        JSONArray rankedDesc = osuUtil.osuSearch("", "ranked_desc", "", session);
        return osuUtil.osu2Malody(from, rankedDesc, session);
    }

    @Override
    public StoreList<Chart> chartList(int sid, int from, HttpSession session) {
        JSONArray beatMapSets = osuUtil.osuSearch("3", "any", "", Integer.toString(sid), "", session);;
        if (beatMapSets == null) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) beatMapSets.get(0);
        JSONArray beatMaps = jsonObject.getJSONArray("beatmaps");
        if (beatMaps.size() > 1) {
            for (int i = 0; i < beatMaps.size(); i++) {
                JSONObject jsonObject1 = beatMaps.getJSONObject(i);
                if (jsonObject1.getIntValue("id") == sid) {
                    jsonObject = jsonObject1;
                    break;
                }
            }
        }
        StoreList<Chart> storeList = new StoreList<>();
        storeList.setHasMore(true);
        storeList.setNext(from + 50);
        List<Chart> chartList = storeList.getData();
        for (int i = 0; i < beatMaps.size(); i++) {
            JSONObject chart = beatMaps.getJSONObject(i);
            chartList.add(new Chart(
                    chart.getIntValue("id"),
                    chart.getIntValue("user_id"),
                    jsonObject.getString("creator"),
                    chart.getString("version"),
                    (int)(chart.getDouble("difficulty_rating") * 100),
                    chart.getIntValue("total_length"),
                    osuUtil.getLevel(chart.getString("status")),
                    0,
                    0
            ));
        }
        return storeList;
    }

    @Override
    public DownloadList download(int cid, HttpSession session) {
        JSONArray array = osuUtil.osuSearch(Integer.toString(3), "", Integer.toString(cid), session);
        DownloadList downloadList = new DownloadList();
        int sid = 0;
        if (array != null) {
            sid = array.getJSONObject(0).getIntValue("id");
            downloadList.setSid(sid);
            downloadList.setCode(0);
            downloadList.setCid(cid);
        } else {
            downloadList.setCode(-2);
            return downloadList;
        }

        // 从官网获取下载链接
        ResponseEntity<String> response = Requests.get("https://osu.ppy.sh/beatmapsets/" + sid + "/download", "https://osu.ppy.sh/beatmapsets", restTemplate);
        ArrayList<DownloadItem> items = new ArrayList<>();
        // 下载链接包含在响应头的location中
        session.setAttribute("link", response.getHeaders().getFirst(HttpHeaders.LOCATION));
        // 构造列表并返回
        session.setAttribute("cid", Integer.toString(cid));
        String downloadUrl = UrlUtil.stripEndSlash(serverUrl) + "/api/store/d";
        items.add(new DownloadItem(cid + ".osu", "0", UrlUtil.getUrl(downloadUrl, "type", "1")));
        items.add(new DownloadItem("audio.mp3", "0", UrlUtil.getUrl(downloadUrl, "type", "2")));
        items.add(new DownloadItem("bg.jpg", "0", UrlUtil.getUrl(downloadUrl, "type", "3")));
        downloadList.setItems(items);
        return downloadList;
    }

    @Override
    public void sendChartFile(int type, HttpServletResponse response, HttpSession session) {
        String cid = (String) session.getAttribute("cid");
        String link = (String) session.getAttribute("link");
        String workPath = tmpPath + File.separator + cid;
        File path = new File(workPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            if (type == 1) {
                sendOsuFile(link, cid, response);
            } else if (type == 2) {
                // 音乐文件可能为ogg格式
                if (!sendRes("mp3", cid, response)) {
                    sendRes("ogg", cid, response);
                }
            } else if (type == 3) {
                // 背景可能为jpeg格式
                if (!sendRes("jpg", cid, response)) {
                    sendRes("jpeg", cid, response);
                }
                FileUtil.del(workPath);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadChartSet(String link, String cid) {
        // 下载osz源文件
        Map<String, String> params = UrlUtil.getParams(link);
        String fileName = StringUtil.stripInvalid(params.get("fs"));
        String downloadPath = tmpPath + File.separator + "osz" + File.separator;
        File path = new File(downloadPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File osz = new File(downloadPath + fileName);
        if (!osz.exists()) {
            downloadService.downloadOsz(link, cid, osz);
            log.log(Level.INFO, fileName + "下载并解压完成");
        }
        // 解压
        File workPath = new File(tmpPath + File.separator + cid);
        // 如果文件被占用则等待，这里的renameTo用于判断osz文件是否被占用
        while (workPath.exists() || !osz.renameTo(osz)) {
            ThreadUtil.sleep(100);
        }
        StreamExtractor streamExtractor = new StreamExtractor(StandardCharsets.UTF_8, osz);
        streamExtractor.extract(workPath);
        streamExtractor.close();

        // 如果未开启缓存则把osz文件删了
        if (!saveTemp) {
            osz.delete();
        }
    }

    private boolean sendRes(String type, String cid, HttpServletResponse response) throws IOException {
        List<File> files = FileUtil.loopFiles(tmpPath + File.separator + cid);
        File res = null;
        for (File file : files) {
            if (FileUtil.getSuffix(file).equals(type)){
                res = file;
                break;
            }
        }
        if (res == null) {
            return false;
        }
        FileInputStream stream = new FileInputStream(res);
        IoUtil.copy(stream, response.getOutputStream());
        stream.close();
        return true;
    }

    private void sendOsuFile(String link, String cid, HttpServletResponse response) throws IOException {
        downloadChartSet(link, cid);
        List<File> files = FileUtil.loopFiles(tmpPath + File.separator + cid);
        File osu = null;
        for (File file : files) {
            String suffix = FileUtil.getSuffix(file);
            if ("osu".equals(suffix)) {
               if (cid.equals(OsuUtil.getOsuFileCid(file))) {
                   osu = file;
                   break;
               }
           }
        }
        if (osu == null) {
            return;
        }
        for (File file : files) {
            String suffix = FileUtil.getSuffix(file);
            if ("jpg".equals(suffix) || "jpeg".equals(suffix)){
                OsuUtil.setOsuFileValue(file.getName(), "bg.jpg", osu);
            } else if ("mp3".equals(suffix) || "ogg".equals(suffix)) {
                OsuUtil.setOsuFileValue(file.getName(), "audio.mp3", osu);
            }
        }
        FileInputStream stream = new FileInputStream(osu);
        IoUtil.copy(stream, response.getOutputStream());
        stream.close();
    }
}
