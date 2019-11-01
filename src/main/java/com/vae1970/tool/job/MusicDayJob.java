package com.vae1970.tool.job;

import com.vae1970.tool.service.MusicService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dongzhou.gu
 * @date 2019/10/30
 * @description
 */
@Component
public class MusicDayJob implements Job {

    @Autowired
    private MusicService musicService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        musicService.move();
    }
}
