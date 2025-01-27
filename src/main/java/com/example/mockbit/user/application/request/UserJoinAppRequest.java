package com.example.mockbit.user.application.request;

import com.example.mockbit.user.domain.User;

public record UserJoinAppRequest(
        String userid,
        String nickname,
        String password
) {

    public User toUser() {
        return User.createUser(userid, nickname, password);
    }
}
