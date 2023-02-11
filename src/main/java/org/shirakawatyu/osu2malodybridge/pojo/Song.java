package org.shirakawatyu.osu2malodybridge.pojo;

public class Song {
    int sid;
    String cover;
    int length;
    double bpm;
    String title;
    String artist;
    int mode;
    long time;

    public Song(int sid, String cover, int length, double bpm, String title, String artist, int mode, long time) {
        this.sid = sid;
        this.cover = cover;
        this.length = length;
        this.bpm = bpm;
        this.title = title;
        this.artist = artist;
        this.mode = mode;
        this.time = time;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
