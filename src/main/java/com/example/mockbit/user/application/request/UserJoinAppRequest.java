package com.example.mockbit.user.application.request;

import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.Userid;

public record UserJoinAppRequest(
        Userid userid,
        String nickname,
        String password
) {

    public User toUser() {
        return User.createUser(String.valueOf(userid), nickname, password);
    }
}
