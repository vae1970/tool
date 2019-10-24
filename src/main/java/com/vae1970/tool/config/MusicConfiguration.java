package com.vae1970.tool.config;

import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.service.MusicService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Configuration
@EnableConfigurationProperties(MusicAccountProperties.class)
@ConditionalOnClass(MusicService.class)
@ConditionalOnProperty(prefix = "hello", value = "enable", matchIfMissing = true)
public class MusicConfiguration {

    @Bean
    public UserInfo userInfo() {
        return new UserInfo();
    }

}
