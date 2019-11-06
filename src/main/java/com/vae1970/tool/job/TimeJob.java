package com.vae1970.tool.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author dongzhou.gu
 * @date 2019/11/6
 */
@Component
@Slf4j
public class TimeJob {

    /**
     * 随机的间隔时间
     */
//    private static final int INTERVAL_IN_SECONDS = 5 * 60 * 60;


    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobDetail movePlaylistJob;

    /**
     * 每天的早上1点，启动延时任务
     *
     * @throws SchedulerException SchedulerException
     */
//    @Scheduled(cron = "0 0 1 * * ? ")
    @Scheduled(cron = "0 * * * * ? ")
    public void musicJob() throws SchedulerException {
        log.info(":11111");

    }

}
