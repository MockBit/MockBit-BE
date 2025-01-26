package com.example.mockbit.user.application;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String userid, String password, String nickname) {
        if (userRepository.findByUserid(userid).isPresent()) {
            throw new MockBitException(MockbitErrorCode.USER_ID_ALREADY_EXIST);
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new MockBitException(MockbitErrorCode.USER_NICKNAME_ALREADY_EXIST);
        }

        return userRepository.save(new User(userid, password, nickname));
    }
}

