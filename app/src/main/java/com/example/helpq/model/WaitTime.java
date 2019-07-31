package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("WaitTime")
public class WaitTime extends ParseObject {

    private String KEY_BLOCKING_TIME = "blockingTime";
    private String KEY_STRETCH_TIME = "stretchTime";
    private String KEY_CURIOSITY_TIME = "curiosityTime";
    private String KEY_BLOCKING_SIZE = "blockingSize";
    private String KEY_STRETCH_SIZE = "stretchSize";
    private String KEY_CURIOSITY_SIZE = "curiositySize";
    public static String KEY_ADMIN_NAME = "adminName";

    public long getBlockingTime() {
        Number num = getNumber(KEY_BLOCKING_TIME);
        return (num != null ? num.longValue() : 0);
    }

    public void setBlockingTime(long blockingTime) {
        put(KEY_BLOCKING_TIME, new Long(blockingTime));
    }

    public long getStretchTime() {
        Number num = getNumber(KEY_STRETCH_TIME);
        return (num != null ? num.longValue() : 0);
    }

    public void setStretchTime(long stretchTime) {
        put(KEY_STRETCH_TIME, new Long(stretchTime));
    }

    public long getCuriosityTime() {
        Number num = getNumber(KEY_CURIOSITY_TIME);
        return (num != null ? num.longValue() : 0);
    }

    public void setCuriosityTime(long curiosityTime) {
        put(KEY_CURIOSITY_TIME, new Long(curiosityTime));
    }

    public long getBlockingSize() {
        Number num = getNumber(KEY_BLOCKING_SIZE);
        return (num != null ? num.longValue() : 0);
    }

    public void setBlockingSize(long blockingSize) {
        put(KEY_BLOCKING_SIZE, new Long(blockingSize));
    }

    public long getStretchSize() {
        Number num = getNumber(KEY_STRETCH_SIZE);
        return (num != null ? num.longValue() : 0);
    }

    public void setStretchSize(long stretchSize) {
        put(KEY_STRETCH_SIZE, new Long(stretchSize));
    }

    public long getCuriositySize() {
        Number num = getNumber(KEY_CURIOSITY_SIZE);
        return (num != null ? num.longValue() : 0);
    }

    public void setCuriositySize(long curiositySize) {
        put(KEY_CURIOSITY_SIZE, new Long(curiositySize));
    }

    public String getAdminName() {
        return getString(KEY_ADMIN_NAME);
    }

    public void setAdminName(String adminName) {
        put(KEY_ADMIN_NAME, adminName);
    }
}
