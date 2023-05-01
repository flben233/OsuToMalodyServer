package org.shirakawatyu.osu2malodybridge.service;

import java.io.File;

public interface DownloadService {

    void downloadOsz(String link, String cid, File osz);
}
