package com.gumdom.boot.infrastructure.cloud;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloudContainerTaskJob {


    @Scheduled(cron = "0/20 * * * * ?")
    public int heartBeatOnMaster() {
        if (CloudContainer.getInstance().isCloudMac() == false) {
            return 0;
        }
        if (CloudContainer.getInstance().heartBeatOnMaster() > 0) {
            return CloudContainer.getInstance().sortOnSlave();
        }

        return 1;
    }

    @Scheduled(cron = "0 1/1 * * * ?")
    public int heartBeatOnSlave() {
        if (CloudContainer.getInstance().isCloudMac() == false) {
            return 0;
        }
        if (CloudContainer.getInstance().heartBeatOnSlave() > 0) {
            return 1;
        }
        return CloudContainer.getInstance().register();
    }



}
