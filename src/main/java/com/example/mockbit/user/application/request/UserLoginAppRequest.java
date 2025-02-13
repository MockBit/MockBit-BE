package com.example.mockbit.user.application.request;

public record UserLoginAppRequest(
        String userid,
        String password
) {
}
