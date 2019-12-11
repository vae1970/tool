package com.vae1970.tool.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vae1970.tool.dto.UserInfo;
import com.vae1970.tool.enums.MusicOp;
import com.vae1970.tool.util.MusicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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
        MusicUtil.checkLogin(userInfo.getUserId());
        JSONArray playlistArray = Optional.ofNullable(userInfo.getUserId())
                .map(i -> MusicUtil.playlist(i, userInfo.getUserId())).map(HttpEntity::getBody)
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

    private void moveItem(String oldPlaylist, String newPlaylist, String userKey) {
        ResponseEntity<String> value = MusicUtil.playlistDetail(oldPlaylist, userKey);
        JSONArray tracks;
        try {
            tracks = Optional.of(value).map(HttpEntity::getBody)
                    .map(JSONObject::parseObject)
                    .map(s -> s.getJSONObject("playlist"))
                    .map(s -> s.getJSONArray("tracks"))
                    .orElse(new JSONArray());
        } catch (Exception e) {
            tracks = new JSONArray();
            System.out.println("-----------------------------------");
            e.printStackTrace();
        }
        System.out.println(value.getBody());
        List<Object> trackList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            Optional.ofNullable(track).map(s -> s.getString("id")).ifPresent(trackList::add);
        }
        if (trackList.size() > 0) {
            ResponseEntity<String> responseEntity = MusicUtil.playlistTracks(MusicOp.add, oldPlaylist, trackList, userKey);
            System.out.println(responseEntity);
            ResponseEntity<String> responseEntity1 = MusicUtil.playlistTracks(MusicOp.del, newPlaylist, trackList, userKey);
            System.out.println(responseEntity1);
        }
    }

    public void move(String userKey) {
        moveItem(userInfo.getTodayPlaylist(), userInfo.getTotalPlaylist(), userKey);
        moveItem(userInfo.getTomorrowPlaylist(), userInfo.getTodayPlaylist(), userKey);
    }

}
