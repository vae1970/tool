package com.vae1970.tool.job;

import com.vae1970.tool.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.vae1970.tool.consts.MusicConst.MOVE_PLAYLIST_JOB_NAME;
import static com.vae1970.tool.consts.MusicConst.MUSIC_GROUP_NAME;

/**
 * @author dongzhou.gu
 * @date 2019/11/6
 */
@Component
@Slf4j
public class TimeJob {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobDetail dayMusic;

    /**
     * 每天的早上1点，启动延时任务
     *
     * @throws SchedulerException SchedulerException
     */
//    @Scheduled(cron = "0 0 1 * * ? ")
    @Scheduled(cron = "0/10 * * * * ? ")
    public void musicJob() throws SchedulerException {
        log.info(":11111");

        TriggerKey triggerKey = TriggerKey.triggerKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME);

        Trigger trigger = MovePlaylistJob.getTrigger();
        Date date = scheduler.rescheduleJob(triggerKey, trigger);
        System.out.println(date);
    }

    @Scheduled(cron = "0 * * * * ? ")
    public void m2() {
        System.out.println(SpringContextUtil.getApplicationContext());
    }

}
