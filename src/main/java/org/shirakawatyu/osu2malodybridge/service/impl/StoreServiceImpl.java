package org.shirakawatyu.osu2malodybridge.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.compress.extractor.StreamExtractor;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.core5.http.HttpHeaders;
import org.shirakawatyu.osu2malodybridge.pojo.*;
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

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class StoreServiceImpl implements StoreService {

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
        items.add(new DownloadItem("FLAG", "0", UrlUtil.getUrl(downloadUrl, "type", "0")));
        items.add(new DownloadItem(cid + ".osu", "0", UrlUtil.getUrl(downloadUrl, "type", "1")));
        items.add(new DownloadItem("audio.mp3", "0", UrlUtil.getUrl(downloadUrl, "type", "2")));
        items.add(new DownloadItem("bg.jpg", "0", UrlUtil.getUrl(downloadUrl, "type", "3")));
        FileUtil.del(tmpPath + File.separator + cid);
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
            if (type == 0) {
                log.log(Level.INFO, "正在下载osz源文件");
                downloadChartSet(link, cid, response);
            } else if (type == 1) {
                log.log(Level.INFO, "正在发送osu文件");
                sendOsuFile(cid, response);
            } else if (type == 2) {
                // 音乐文件可能为ogg格式
                log.log(Level.INFO, "正在发送mp3文件");
                if (!sendRes("mp3", cid, response)) {
                    sendRes("ogg", cid, response);
                }
            } else if (type == 3) {
                // 背景可能为jpeg格式
                log.log(Level.INFO, "正在发送jpg文件");
                if (!sendRes("jpg", cid, response)) {
                    sendRes("jpeg", cid, response);
                }
            }
            log.log(Level.INFO, "完成");
        }catch (IOException e) {
            FileUtil.del(workPath);
            throw new RuntimeException(e);
        }
    }

    public void downloadChartSet(String link, String cid, HttpServletResponse response) throws IOException {
        String workPath = tmpPath + File.separator + cid + File.separator;
        // 下载osz源文件
        URL url = new URL(link);
        Map<String, String> params = UrlUtil.getParams(link);
        URLConnection urlConnection = null;
        if (httpProxy.isEnable()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort()));
            urlConnection = url.openConnection(proxy);
        } else {
            urlConnection = url.openConnection();
        }
        InputStream inputStream = urlConnection.getInputStream();
        String fileName = StringUtil.stripInvalid(params.get("fs"));
        File osz = new File(workPath + fileName);
        if (!osz.exists()) {
            osz.createNewFile();
        }
        FileOutputStream fs = new FileOutputStream(osz);
        IoUtil.copy(inputStream, fs);
        fs.close();

        // 解压
        StreamExtractor streamExtractor = new StreamExtractor(StandardCharsets.UTF_8, new File(workPath + fileName));
        streamExtractor.extract(new File(workPath));
        log.log(Level.INFO, "osz文件下载并解压完成");

        // 发送完成信号
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(new byte[1024]);
        outputStream.close();
    }

    public boolean sendRes(String type, String cid, HttpServletResponse response) throws IOException {
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
        IoUtil.copy(new FileInputStream(res), response.getOutputStream());
        return true;
    }

    public void sendOsuFile(String cid, HttpServletResponse response) throws IOException {
        List<File> files = FileUtil.loopFiles(tmpPath + File.separator + cid);
        File osu = null;
        for (File file : files) {
           if ("osu".equals(FileUtil.getSuffix(file))) {
               if (cid.equals(OsuUtil.getOsuFileCid(file))) {
                   osu = file;
                   break;
               }
           }
        }
        if (osu == null) {
            return;
        }
        OsuUtil.setOsuFileValue("AudioFilename", "audio.mp3", osu);
        IoUtil.copy(new FileInputStream(osu), response.getOutputStream());
    }

}
