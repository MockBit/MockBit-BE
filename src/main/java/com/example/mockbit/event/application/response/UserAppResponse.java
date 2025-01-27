package com.example.mockbit.event.application.response;

import com.example.mockbit.event.domain.event.Password;
import com.example.mockbit.user.domain.Nickname;
import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.Userid;

public record UserAppResponse(
        Userid userid,
        Nickname nickname
) {

    public static UserAppResponse of(User user) {
        return new UserAppResponse(user.getUserid(), user.getNickname());
    }
}
