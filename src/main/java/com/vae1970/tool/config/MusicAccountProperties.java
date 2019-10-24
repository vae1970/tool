package com.vae1970.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Data
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "music")
public class MusicAccountProperties {

    private String phone;

    private String password;

}
