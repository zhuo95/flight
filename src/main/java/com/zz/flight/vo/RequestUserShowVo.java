package com.zz.flight.vo;

import com.zz.flight.entity.Request;
import com.zz.flight.entity.User;

import java.util.List;

public class RequestUserShowVo {
    private Request request;

    private List<User> likedUser;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<User> getLikedUser() {
        return likedUser;
    }

    public void setLikedUser(List<User> likedUser) {
        this.likedUser = likedUser;
    }
}
