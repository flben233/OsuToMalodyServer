package org.shirakawatyu.osu2malodybridge.pojo;

public class Chart {
    int cid;
    int uid;
    String creator;
    String version;
    int level;
    int length;
    int type;
    int size;
    int mode;

    public Chart(int cid, int uid, String creator, String version, int level, int length, int type, int size, int mode) {
        this.cid = cid;
        this.uid = uid;
        this.creator = creator;
        this.version = version;
        this.level = level;
        this.length = length;
        this.type = type;
        this.size = size;
        this.mode = mode;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
