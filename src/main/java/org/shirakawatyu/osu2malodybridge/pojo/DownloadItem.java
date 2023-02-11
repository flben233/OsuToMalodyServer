package org.shirakawatyu.osu2malodybridge.pojo;

public class DownloadItem {
    String name;
    String hash;
    String file;

    public DownloadItem(String name, String hash, String file) {
        this.name = name;
        this.hash = hash;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "DownloadItem{" +
                "name='" + name + '\'' +
                ", hash='" + hash + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
