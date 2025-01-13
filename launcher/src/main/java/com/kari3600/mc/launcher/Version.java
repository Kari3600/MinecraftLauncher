package com.kari3600.mc.launcher;

public class Version {
    enum VersionType{snapshot,release,old_beta,old_alpha}
    final String id;
    final VersionType type;
    final String url;
    final String time;
    final String releaseTime;
    public Version(String id, VersionType type, String url, String time, String releaseTime) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.time = time;
        this.releaseTime = releaseTime;
    }
    @Override
    public String toString() {
        return id;
    }
}
