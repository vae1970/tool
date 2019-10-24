package com.vae1970.tool.config;

import com.alibaba.fastjson.JSONObject;
import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Component
public class MyContextStartedEvent implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private MusicService musicService;

    @Autowired
    private UserInfo userInfo;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        musicService.init();
        System.out.println(JSONObject.toJSONString(userInfo));
    }
}
