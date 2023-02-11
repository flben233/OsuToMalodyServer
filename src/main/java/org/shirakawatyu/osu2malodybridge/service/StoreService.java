package org.shirakawatyu.osu2malodybridge.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.shirakawatyu.osu2malodybridge.pojo.Chart;
import org.shirakawatyu.osu2malodybridge.pojo.DownloadList;
import org.shirakawatyu.osu2malodybridge.pojo.Song;
import org.shirakawatyu.osu2malodybridge.pojo.StoreList;


public interface StoreService {
    StoreList<Song> search(String word, int from, int beta, HttpSession session);
    StoreList<Song> promote(int from, HttpSession session);
    StoreList<Chart> chartList(int sid, int from, HttpSession session);
    DownloadList download(int cid, HttpSession session);
    void sendChartFile(int type, HttpServletResponse response, HttpSession session);
}
