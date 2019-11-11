package com.vae1970.tool.job;

import com.vae1970.tool.config.MusicAccountProperties;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private MusicAccountProperties musicAccountProperties;

    private static final Map<JobDetail, Set<? extends Trigger>> TRIGGERS_AND_JOBS = new ConcurrentHashMap<>(1);

    /**
     * 每天的早上1点，启动延时任务
     *
     * @throws SchedulerException SchedulerException
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void musicJob() throws SchedulerException {
        Set<Trigger> triggers = Stream.of(MovePlaylistJob.getTrigger()).collect(Collectors.toSet());
        TRIGGERS_AND_JOBS.put(dayMusic, triggers);
        scheduler.scheduleJobs(TRIGGERS_AND_JOBS, true);
    }

    @Scheduled(cron = "0/1 * * * * ? ")
    public void job() {
        System.out.println("s-s-s-s-s-s--s-s");
        System.out.println(musicAccountProperties);
        System.out.println("s-s-s-s-s-s--s-s");

    }

}
