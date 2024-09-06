package com.gumdom.boot.infrastructure.cloud;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CloudContainer {

    private static CloudContainer instance = new CloudContainer();
    private static String wuhu;

    public static CloudContainer getInstance() {
        return instance;
    }

    CloudContainer() {
        this.wuhu = System.getProperty("env_mode_cloud_mac");
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isMaster() {
        return this.isMaster(getHostName());
    }

    public boolean isMaster(String hostName) {
        //从缓存中查询当前节点是否master节点;
        if (hostName.equalsIgnoreCase("caching_HostName")) {
            return true;
        }
        return false;
    }

    public boolean isSlave(String num) {
        return this.isSlave(getHostName(), num);
    }

    public boolean isSlave(String hostName, String num) {
        if (StringUtils.isEmpty(hostName)) {
            return false;
        }
        if ("SLAVE".concat(num).equalsIgnoreCase("caching_slaveName")) {
            return true;
        }
        return false;
    }

    public boolean isCloudMac(){
        return this.isCloudMac(getHostName());
    }

    public boolean isCloudMac(String hostName) {
        if (StringUtils.isEmpty(hostName)) {
            return false;
        }
        return StringUtils.contains(hostName.toLowerCase(),"cloud_app");
    }

    public int heartBeatOnMaster(){
        return this.heartBeatOnMaster(getHostName());
    }

    /**
     * 每30秒做一次心跳,触发间隔是20秒,提前10秒抢占资源;
     */
    public int heartBeatOnMaster(String hostName) {
        if (StringUtils.isEmpty(hostName)) {
            return 0;
        }
        //update dict_info set modity_time = sysdate + 0.5 / (24*60) where type = 'cloud_config' and code = '' and value = ''
        return 1;
    }

    public int heartBeatOnSlave(){
        return this.heartBeatOnSlave(getHostName());
    }

    public int heartBeatOnSlave(String hostName) {
        if (StringUtils.isEmpty(hostName)) {
            return 0;
        }
        //List<String> list = 缓存数据;
        //list.stream().filter(t-> "slave".equalsIgnoreCase(t)).findFirst();
        //update dict_info set modity_time = sysdate + 0.5 / (24*60) where type = 'cloud_config' and code = '' and value = ''
        return 1;
    }



}
