package com.hx.manixchen.views;

/**
 * Created by manixchen on 2016/12/6.
 */

public class ThreadInfoDownloadList {
    private String appName;
    private  String version;
    private  String appSize;
    private  String appIcon;
    private  String downloadAddress;
    private  int downloadTimes;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getDownloadAddress() {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }

    public int getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(int downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public ThreadInfoDownloadList() {
    }

    public ThreadInfoDownloadList(String appName, String version, String appSize, String appIcon, String downloadAddress, int downloadTimes) {
        this.appName = appName;
        this.version = version;
        this.appSize = appSize;
        this.appIcon = appIcon;
        this.downloadAddress = downloadAddress;
        this.downloadTimes = downloadTimes;
    }
}
