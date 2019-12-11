package com.vae1970.tool.config;

import com.vae1970.tool.service.CacheService;
import com.vae1970.tool.service.MusicService;
import lombok.SneakyThrows;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import static com.vae1970.tool.consts.MusicConst.MOVE_PLAYLIST_JOB_NAME;
import static com.vae1970.tool.consts.MusicConst.MUSIC_GROUP_NAME;

/**
 * @author dongzhou.gu
 * @date 2019/12/11
 */
@Component
public class ApplicationEventListener<E extends ApplicationEvent> implements ApplicationListener<E> {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private Scheduler scheduler;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            init();
        } else if (event instanceof ContextClosedEvent) {
            close();
        }
    }

    private void init() throws SchedulerException {
        cacheService.load();
        musicService.init();
        //  先移除旧任务，再开启新任务
        TriggerKey triggerKey = TriggerKey.triggerKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(JobKey.jobKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME));
    }

    private void close() {
        cacheService.sync();
    }

}
