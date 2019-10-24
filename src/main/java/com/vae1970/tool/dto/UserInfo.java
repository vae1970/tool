package com.vae1970.tool.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Data
public class UserInfo implements Serializable {

    private String userId;

    private String tomorrowPlaylist;

    private String todayPlaylist;

    private String totalPlaylist;

}
