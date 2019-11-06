package com.vae1970.tool.config;

import com.alibaba.fastjson.JSONObject;
import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.job.MovePlaylistJob;
import com.vae1970.tool.service.MusicService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static com.vae1970.tool.consts.MusicConst.*;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Component
public class InitEvent implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MusicService musicService;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private MusicAccountProperties musicAccountProperties;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println(JSONObject.toJSONString(musicAccountProperties));

        try {
            init();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(userInfo));
    }

    private void init() throws SchedulerException {
        //        musicService.init();
        //  先移除旧任务，再开启新任务
        TriggerKey triggerKey = TriggerKey.triggerKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(JobKey.jobKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME));


        JobDetail movePlaylistJob = JobBuilder.newJob(MovePlaylistJob.class).withIdentity("movePlaylistJob", "musicJobGroup")
                .storeDurably().build();

        long intervalSeconds = new Random().longs(1, 0, INTERVAL_IN_SECONDS).findFirst().orElse(0L);
        LocalDateTime currentTime = LocalDateTime.now().plusSeconds(intervalSeconds);
        Date startAt = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        Trigger trigger = newTrigger().startAt(startAt).withIdentity(UUID.randomUUID().toString(), "musicJobGroup")
                .withSchedule(SimpleScheduleBuilder//SimpleScheduleBuilder是简单调用触发器，它只能指定触发的间隔时间和执行次数；
                        .simpleSchedule()//创建一个SimpleScheduleBuilder
                        .withIntervalInSeconds(10)//指定一个重复间隔,以毫秒为单位。
                        .withRepeatCount(10))
                .build();
        scheduler.scheduleJob(movePlaylistJob, trigger);
        scheduler.start();

    }

}
