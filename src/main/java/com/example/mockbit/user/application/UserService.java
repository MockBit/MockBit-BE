package com.example.mockbit.user.application;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.common.exception.AuthenticationException;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.user.application.response.UserAppResponse;
import com.example.mockbit.user.application.request.UserJoinAppRequest;
import com.example.mockbit.user.application.request.UserUpdateAppRequest;
import com.example.mockbit.user.domain.Nickname;
import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.Userid;
import com.example.mockbit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long join(UserJoinAppRequest request) {
        if (userRepository.findByUserid(new Userid(request.userid())).isPresent()) {
            throw new MockBitException(MockbitErrorCode.USER_ID_ALREADY_EXIST);
        }

        if (userRepository.findByNickname(new Nickname(request.nickname())).isPresent()) {
            throw new MockBitException(MockbitErrorCode.USER_NICKNAME_ALREADY_EXIST);
        }

        User user = request.toUser();

        userRepository.saveAndFlush(user);
        accountService.createAccountForUser(user);

        return user.getId();
    }

    @Transactional(readOnly = true)
    public void validateUser(Userid userid, String rawPassword) {
        User user = getUserByUserid(userid);

        if (user.isPasswordMismatch(rawPassword)) {
            throw new AuthenticationException(MockbitErrorCode.ID_PASSWORD_INVALID);
        }
    }

    @Transactional
    public void updateUser(UserUpdateAppRequest request) {
        User user = getUserByUserid(new Userid(request.userid()));

        if (request.isNicknameExists() && !user.getNickname().getValue().equals(request.nickname())) {
            if (userRepository.findByNickname(new Nickname(request.nickname())).isPresent()) {
                throw new MockBitException(MockbitErrorCode.USER_NICKNAME_ALREADY_EXIST);
            }
            user.changeNickname(request.nickname());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.changePassword(request.password());
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        eventPublisher.publishEvent(new UserDeleteEvent(id));
    }

    @Transactional(readOnly = true)
    public UserAppResponse findById(Long id) {
        User user = getUserById(id);
        return UserAppResponse.of(user);
    }

    private User getUserByUserid(Userid userid) {
        return userRepository.findByUserid(userid)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.ID_PASSWORD_INVALID));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.NO_RESOURCE_REQUEST));
    }
}

