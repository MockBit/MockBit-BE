package com.example.mockbit.user.application.request;

import com.example.mockbit.user.domain.User;

public record UserJoinAppRequest(
        String userid,
        String password,
        String nickname
) {

    public User toUser() {
        return User.createUser(userid, password, nickname);
    }
}
