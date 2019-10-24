package com.vae1970.tool.util;

import com.alibaba.fastjson.JSONObject;
import com.vae1970.tool.config.MusicAccountProperties;
import com.vae1970.tool.consts.MusicConst;
import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.enums.MusicOp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@SuppressWarnings("WeakerAccess")
public class MusicUtil {

    /**
     * login by phone
     *
     * @param phone    phone number
     * @param password password
     * @return ResponseEntity
     */
    public static ResponseEntity<String> loginByPhone(String phone, String password) {
        Map<String, String> map = new HashMap<>(2);
        map.put("phone", phone);
        map.put("password", password);
        return doGet(map, MusicConst.LOGIN_URL);
    }

    /**
     * get login status
     *
     * @return ResponseEntity
     */
    public static ResponseEntity<String> loginStatus() {
        return doGet(null, MusicConst.LOGIN_STATUS_URL);
    }

    /**
     * refresh login status
     *
     * @return ResponseEntity
     */
    public static ResponseEntity<String> refreshLogin() {
        return doGet(null, MusicConst.REFRESH_LOGIN_URL);
    }

    /**
     * get playlist by userId
     *
     * @return ResponseEntity
     */
    public static ResponseEntity<String> playlist(String uid) {
        Map<String, String> map = new HashMap<>(1);
        map.put("uid", uid);
        return doGet(map, MusicConst.PLAYLIST_URL);
    }

    /**
     * get playlist detail
     *
     * @param playlistId playlistId
     * @return ResponseEntity
     */
    public static ResponseEntity<String> playlistDetail(String playlistId) {
        Map<String, String> map = new HashMap<>(1);
        map.put("id", playlistId);
        return doGet(map, MusicConst.PLAYLIST_DETAIL_URL);
    }

    /**
     * add or delete tracks
     *
     * @param op        op
     * @param pid       playlistId
     * @param trackList tracks
     * @return ResponseEntity
     */
    public static ResponseEntity<String> playlistTracks(MusicOp op, String pid, List<Object> trackList) {
        Map<String, String> map = new HashMap<>(3);
        map.put("op", op.name());
        map.put("pid", pid);
        map.put("tracks", trackList.stream().map(Object::toString).collect(Collectors.joining(",")));
        return doGet(map, MusicConst.PLAYLIST_TRACK_URL);
    }

    /**
     * check login
     *
     * @return boolean
     */
    public static boolean checkLogin() {
        ResponseEntity<String> loginStatus = MusicUtil.loginStatus();
        if (!HttpStatus.OK.equals(loginStatus.getStatusCode())) {
            ResponseEntity<String> refreshLogin = MusicUtil.refreshLogin();
            if (!HttpStatus.OK.equals(refreshLogin.getStatusCode())) {
                MusicAccountProperties account = SpringContextUtil.getBean(MusicAccountProperties.class);
                ResponseEntity<String> login = MusicUtil.loginByPhone(account.getPhone(), account.getPassword());
                boolean success = HttpStatus.OK.equals(login.getStatusCode());
                if (success) {
                    Optional.ofNullable(login.getBody()).map(JSONObject::parseObject).map(i -> i.getJSONObject("account"))
                            .map(i -> i.get("id")).map(Object::toString)
                            .ifPresent(id -> {
                                UserInfo userInfo = SpringContextUtil.getBean("userInfo");
                                if (userInfo != null) {
                                    userInfo.setUserId(id);
                                }
                            });
                }
                return success;
            }
        }
        return true;
    }

    /**
     * if auth failed, then retry
     *
     * @param params params
     * @param uri    uri
     * @return ResponseEntity
     */
    private static ResponseEntity<String> doGet(Map<String, String> params, String uri) {
        ResponseEntity<String> responseEntity = HttpUtil.doGet(params, uri);
        if (responseEntity.getStatusCode().equals(HttpStatus.MOVED_PERMANENTLY)) {
            if (checkLogin()) {
                return HttpUtil.doGet(params, uri);
            }
        }
        return responseEntity;
    }

}
