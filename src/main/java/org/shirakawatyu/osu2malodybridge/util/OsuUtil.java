package org.shirakawatyu.osu2malodybridge.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.shirakawatyu.osu2malodybridge.config.RestTemplateConfig;
import org.shirakawatyu.osu2malodybridge.pojo.Song;
import org.shirakawatyu.osu2malodybridge.pojo.StoreList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OsuUtil {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BasicCookieStore basicCookieStore;
    @Value("${malody.server.showAll}")
    boolean showAll;
    @Value("${malody.osu.clientID}")
    String clientId;
    @Value("${malody.osu.clientSecret}")
    String clientSecret;
    long lastLogin = 0;

    /**
     * 搜索谱面
     *
     * @param m    对应官网的模式，3代表mania
     * @param sort 对应官网的排序方式
     * @param q    查询字符串，对应官网的搜索栏
     * @return 搜到的谱面，为空时返回null
     */
    public JSONArray osuSearch(String m, String sort, String q, HttpSession session) {
        return osuSearch(m, "", sort, q, "", session);
    }

    /**
     * 搜索谱面
     *
     * @param m    对应官网的模式，3代表mania
     * @param sort 对应官网的排序方式
     * @param q    查询字符串，对应官网的搜索栏
     * @param c    对应官网搜索页面的“常规“一栏
     * @return 搜到的谱面，为空时返回null
     */
    public JSONArray osuSearch(String m, String s, String sort, String q, String c, HttpSession session) {
        if (!q.equals(session.getAttribute("query"))) {
            session.removeAttribute("cursor_string");
        }
        String cursorString = (String) session.getAttribute("cursor_string");
        String url = "https://osu.ppy.sh/api/v2/beatmapsets/search?e=&g=&l=&nsfw=&played=&r=" +
                "&s=" +
                s +
                "&c=" +
                c +
                "&m=" +
                m +
                "&q=" +
                q +
                "&sort=" +
                sort +
                "&cursor_string=" +
                cursorString;
        ResponseEntity<String> beatResp = Requests.get(url, "", restTemplate);
        JSONObject jsonObject1 = JSON.parseObject(beatResp.getBody());
        if (jsonObject1 == null) {
            return null;
        }
        session.setAttribute("cursor_string", jsonObject1.getString("cursor_string"));
        session.setAttribute("query", q);
        return jsonObject1.getJSONArray("beatmapsets");
    }

    /**
     * 登录，会自动判断登录是否过期，如果登录过期才进行登录
     */
    public void autoLogin() {
        if (lastLogin == 0 || System.currentTimeMillis() - lastLogin > 86400000) {
            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("grant_type", "client_credentials");
            params.add("scope", "public");
            ResponseEntity<String> token = Requests.post("https://osu.ppy.sh/oauth/token", params, "", restTemplate);
            if (token.getStatusCode().is2xxSuccessful()) {
                Requests.setToken(JSON.parseObject(token.getBody()).getString("access_token"));
                Logger.getLogger("o.s.o.u.OsuUtil").log(Level.INFO, "登录成功");
                lastLogin = System.currentTimeMillis();
            }
        }
    }

    /**
     * 获取歌曲长度
     * @param jsonObject osuSearch()搜索到的结果
     * @return 歌曲长度，单位秒
     */
    public int getLength(JSONObject jsonObject) {
        JSONObject beatmaps = (JSONObject) jsonObject.getJSONArray("beatmaps").get(0);
        return beatmaps.getIntValue("total_length");
    }

    /**
     * osu的谱面列表转换成malody的返回列表
     *
     * @param from        定义详见<a href="https://gitlab.com/mugzone_team/malody_store_api/-/blob/main/README_CN.md">malody开发文档</a>
     * @param beatMapSets osuSearch()搜索到的结果
     * @return 转换好的返回列表，结构详见<a href="https://gitlab.com/mugzone_team/malody_store_api/-/blob/main/README_CN.md">malody开发文档</a>
     */
    public StoreList<Song> osu2Malody(int from, JSONArray beatMapSets, HttpSession session) {
        if (beatMapSets == null) {
            return null;
        }
        StoreList<Song> storeList = new StoreList<>();
        List<Song> songList = storeList.getData();
        storeList.setHasMore(session.getAttribute("cursor_string") != null);
        storeList.setNext(from + 50);
        for (int i = 0; i < beatMapSets.size(); i++) {
            JSONObject jsonObject = beatMapSets.getJSONObject(i);
            songList.add(new Song(
                    jsonObject.getIntValue("id"),
                    JSON.parseObject(jsonObject.getString("covers")).getString("list@2x"),
                    getLength(jsonObject),
                    jsonObject.getDouble("bpm"),
                    jsonObject.getString("title"),
                    jsonObject.getString("artist"),
                    0,
                    0)
            );
        }
        return storeList;
    }

    /**
     * 从.osu文件中取得谱面id
     *
     * @param file .osu文件
     * @return 谱面id
     */
    public static String getOsuFileCid(File file) {
        BufferedReader reader = FileUtil.getUtf8Reader(file);
        try (reader) {
            while (true) {
                String line = null;
                try {
                    if (!reader.ready()) {
                        break;
                    }
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int beatmapID = line.indexOf("BeatmapID");
                if (beatmapID != -1) {
                    return line.split(":")[1];
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 替换文件中某个值
     *
     * @param oldValue 旧值
     * @param newValue 新值
     * @param file     .osu文件
     */
    public static void setOsuFileValue(String oldValue, String newValue, File file) {
        List<String> strings = FileUtil.readLines(file, StandardCharsets.UTF_8);
        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i);
            if (string.contains(oldValue)) {
                string = string.replace(oldValue, newValue);
                strings.set(i, string);
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string).append("\n");
        }
        FileUtil.writeUtf8String(stringBuilder.toString(), file);
    }

    /**
     * 将osu谱面的状态转换成malody的状态，ranked和loved对应malody中的stable，其余均为beta
     * <br>
     * 可以通过配置文件中的showAll属性将所有的谱面置为stable
     *
     * @param status osu谱面状态
     * @return 2: stable, 1: beta
     */
    public int getLevel(String status) {
        if (showAll) {
            return 2;
        }
        if (status.equals("ranked") || status.equals("loved")) {
            return 2;
        }
        return 1;
    }
}
