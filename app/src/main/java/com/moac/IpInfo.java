package com.moac;

import java.io.Serializable;

public class IpInfo implements Serializable {
    private String wall;
    private String ipAddr;

    public IpInfo() {
    }

    public IpInfo(String wall, String ipAddr) {
        this.wall = wall;
        this.ipAddr = ipAddr;
    }

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }
}
