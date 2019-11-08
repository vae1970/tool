package com.vae1970.tool.job;

import com.vae1970.tool.service.MusicService;
import com.vae1970.tool.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

import static com.vae1970.tool.consts.MusicConst.*;

/**
 * @author dongzhou.gu
 * @date 2019/10/30
 */
@Slf4j
public class MovePlaylistJob implements Job {

    @Autowired
    private MusicService musicService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("222222:");
//        musicService.move();
    }

    public static Trigger getTrigger() {
        long intervalSeconds = new Random().longs(1, 0, INTERVAL_IN_SECONDS).findFirst().orElse(0L);
//        LocalDateTime currentTime = LocalDate.now().atStartOfDay().plusHours(1).plusSeconds(intervalSeconds);
        LocalDateTime currentTime = LocalDateTime.now().plusSeconds(intervalSeconds);
        log.info(currentTime.toString());
        Date startAt = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());

        TriggerKey triggerKey = TriggerKey.triggerKey(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME);

        Scheduler scheduler = SpringContextUtil.getBean(Scheduler.class);
        Trigger trigger = null;
        try {
            trigger = scheduler.getTrigger(triggerKey);
        } catch (SchedulerException ignored) {
        }
        if (trigger == null) {
            return TriggerBuilder.newTrigger().startAt(startAt).withIdentity(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME).build();
        } else {
            return trigger.getTriggerBuilder().startAt(startAt).withIdentity(MOVE_PLAYLIST_JOB_NAME, MUSIC_GROUP_NAME).build();
        }
    }

}
