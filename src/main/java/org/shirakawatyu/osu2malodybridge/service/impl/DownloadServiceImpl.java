package org.shirakawatyu.osu2malodybridge.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.shirakawatyu.osu2malodybridge.pojo.HttpProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class DownloadServiceImpl implements org.shirakawatyu.osu2malodybridge.service.DownloadService {
    @Autowired
    HttpProxy httpProxy;
    @Value("${malody.server.tmp}")
    String tmpPath;
    ConcurrentHashSet<String> downloadList = new ConcurrentHashSet<>();
    /**
     * 下载osz源文件
     * @param link 下载链接
     * @param cid 谱面id
     * @param osz 下载到的路径
     * @author ShirakawaTyu
     */
    @Override
    public void downloadOsz(String link, String cid, File osz) {
        downloadList.add(link);
        URLConnection urlConnection = null;
        try {
            URL url = new URL(link);
            if (httpProxy.isEnable()) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort()));
                urlConnection = url.openConnection(proxy);
            } else {
                urlConnection = url.openConnection();
            }
        } catch (IOException e) {
            downloadList.remove(link);
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = urlConnection.getInputStream();
             FileOutputStream fs = new FileOutputStream(osz)) {
            if (!osz.exists()) {
                osz.createNewFile();
            }
            IoUtil.copy(inputStream, fs);
        } catch (Exception e) {
            osz.delete();
            downloadList.remove(link);
            throw new RuntimeException(e);
        }
        downloadList.remove(link);
    }
}
