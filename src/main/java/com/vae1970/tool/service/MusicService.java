package com.vae1970.tool.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.enums.MusicOp;
import com.vae1970.tool.util.MusicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Service
public class MusicService {

    @Autowired
    private UserInfo userInfo;

    public void init() {
        MusicUtil.checkLogin();
        JSONArray playlistArray = Optional.ofNullable(userInfo.getUserId()).map(MusicUtil::playlist).map(HttpEntity::getBody)
                .map(JSONObject::parseObject).map(s -> s.getJSONArray("playlist")).orElse(new JSONArray());
        for (int i = 0; i < playlistArray.size(); i++) {
            JSONObject playlist = playlistArray.getJSONObject(i);
            String playlistName = Optional.ofNullable(playlist).map(s -> s.getString("name")).orElse("");
            String id = Optional.ofNullable(playlist).map(s -> s.getString("id")).orElse("");
            switch (playlistName) {
                case "日推 for 小宋":
                    userInfo.setTodayPlaylist(id);
                    break;
                case "i met you, i miss you, i love you~":
                    userInfo.setTotalPlaylist(id);
                    break;
                case "日推 for tomorrow":
                    userInfo.setTomorrowPlaylist(id);
                    break;
                default:
                    break;
            }
        }
    }

    private void moveItem(String oldPlaylist, String newPlaylist) {
        JSONArray tracks = Optional.ofNullable(MusicUtil.playlistDetail(oldPlaylist)).map(HttpEntity::getBody)
                .map(s -> JSONObject.parseObject("playlist")).map(s -> JSONObject.parseArray("tracks"))
                .orElse(new JSONArray());
        List<Object> trackList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            Optional.ofNullable(track).map(s -> s.getString("id")).ifPresent(trackList::add);
        }
        MusicUtil.playlistTracks(MusicOp.add, oldPlaylist, trackList);
        MusicUtil.playlistTracks(MusicOp.del, newPlaylist, trackList);
    }

    public void move() {
        moveItem(userInfo.getTodayPlaylist(), userInfo.getTotalPlaylist());
        moveItem(userInfo.getTomorrowPlaylist(), userInfo.getTodayPlaylist());
    }

}
