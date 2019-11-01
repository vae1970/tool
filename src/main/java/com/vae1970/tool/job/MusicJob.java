package com.vae1970.tool.job;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
public class MusicJob {

    public void job(){
        Trigger trigger = TriggerBuilder.newTrigger().build();
    }

}
