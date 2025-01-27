package com.example.mockbit.user.application.request;

public record UserUpdateAppRequest(
        String userid,
        String nickname,
        String password
) {

    public boolean isNicknameExists() {
        return nickname != null && !nickname.isBlank();
    }
}
