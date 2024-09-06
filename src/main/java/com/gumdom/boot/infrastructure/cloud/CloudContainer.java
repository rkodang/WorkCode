package com.gumdom.boot.infrastructure.cloud;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

    public int sortOnSlave(){
        List<String> slaveList = new ArrayList<>();//从缓存中取数;
        List<String> newSortList = new ArrayList<>();
        for (int i = 1; i < slaveList.size(); i++) {
            newSortList.add("SLAVE" + i);
        }
        List<String> newNameList = new ArrayList<>(newSortList.size());
        for (int i = 1; i < slaveList.size(); i++) {
            String slave = slaveList.get(i - 1);
            if (newSortList.stream().anyMatch(t->t.equals(slave))) {
                slaveList.remove(slave);
                continue;
            }
            newNameList.add(slave);
        }
        //清除slave,重新插入newNameList作为slave
        return 1;
    }

    public int decide(){
        return this.decide(getHostName());
    }

    public int decide(String hostname){
        if (StringUtils.isEmpty(hostname)) {
            return 0;
        }
        //1.mapper执行清除Master节点的操作;
        //2.master机变更为当前将以当前hostName的主机作为master机
        return 1;
    }

    public int register(){
        return this.register(getHostName());
    }

    public int register(String hostName){

        //1.从缓存获取所有slave节点
        //2.删除缓存内的所有slave节点'
        //3.从数据库内重新查数据,若存在当前hostName的数据直接返回

        //4.若hostName的主机是新增加的节点则尝试注册;

        return this.tryRegister(hostName);
    }

    public int tryRegister(String hostName) {
        //将数据注册到数据库
        return 1;
    }

}
