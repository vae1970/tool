package com.vae1970.tool.consts;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
public class MusicConst {

    /**
     * 网易云域名
     */
    private static final String DOMAIN = "http://127.0.0.1:3000";
    /**
     * 手机登录
     */
    public static final String LOGIN_URL = DOMAIN + "/login/cellphone";
    /**
     * 刷新登录
     */
    public static final String REFRESH_LOGIN_URL = DOMAIN + "/login/refresh";
    /**
     * 用户歌单列表
     */
    public static final String PLAYLIST_URL = DOMAIN + "/user/playlist";
    /**
     * 歌单详情
     */
    public static final String PLAYLIST_DETAIL_URL = DOMAIN + "/playlist/detail";
    /**
     * 歌单添加或删除歌曲
     */
    public static final String PLAYLIST_TRACK_URL = DOMAIN + "/playlist/tracks";
    /**
     * 登录状态
     */
    public static final String LOGIN_STATUS_URL = DOMAIN + "/login/status";

    public static final String MOVE_PLAYLIST_JOB_NAME = "movePlaylist";

    public static final String MUSIC_GROUP_NAME = "movePlaylistJob";

    public static final int INTERVAL_IN_SECONDS = 3 * 60 * 60;

}
