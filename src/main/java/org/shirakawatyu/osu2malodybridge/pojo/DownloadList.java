package org.shirakawatyu.osu2malodybridge.pojo;

import java.util.List;

public class DownloadList {
    int code;
    List<DownloadItem> items;


    int sid;
    int cid;


    public DownloadList() {
    }

    public DownloadList(int code, List<DownloadItem> items, int sid, int cid) {
        this.code = code;
        this.items = items;
        this.sid = sid;
        this.cid = cid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DownloadItem> getItems() {
        return items;
    }

    public void setItems(List<DownloadItem> items) {
        this.items = items;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

}
