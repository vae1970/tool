package com.vae1970.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Data
@Validated
@ConfigurationProperties(prefix = "music")
public class MusicAccountProperties implements Serializable {

    @NotNull
    private String phone;

    @NotNull
    private String password;

}
