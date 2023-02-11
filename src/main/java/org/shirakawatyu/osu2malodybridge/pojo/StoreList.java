package org.shirakawatyu.osu2malodybridge.pojo;

import java.util.ArrayList;
import java.util.List;

public class StoreList<T> {
    int code = 0;
    boolean hasMore;
    int next;
    List<T> data;

    public StoreList() {
        data = new ArrayList<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
