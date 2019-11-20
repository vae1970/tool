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

import static com.vae1970.tool.consts.MusicConst.MOVE_PLAYLIST_JOB_NAME;
import static com.vae1970.tool.consts.MusicConst.MUSIC_GROUP_NAME;

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

    @Autowired
    private JobDetail dayMusic;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println(userInfo);
        System.out.println(JSONObject.toJSONString(musicAccountProperties));
        try {
            init();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(this.userInfo));
    }

    private void init() throws SchedulerException {
        musicService.init();
        //  先移除旧任务，再开启新任务
        TriggerKey triggerKey = TriggerKey.triggerKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(JobKey.jobKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME));

        Trigger trigger = MovePlaylistJob.getTrigger();
        scheduler.scheduleJob(dayMusic, trigger);
    }

}
