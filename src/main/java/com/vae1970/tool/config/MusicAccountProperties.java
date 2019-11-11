package com.vae1970.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Data
@Validated
@ConfigurationProperties(prefix = "music")
public class MusicAccountProperties {

    //    @NotNull
    private String phone;

    //    @NotNull
    private String password;

}
