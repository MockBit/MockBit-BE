package com.example.mockbit.user.domain;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.user.domain.repository.UserRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@Embeddable
public class Nickname {

    public static final int NICKNAME_MAX_NAME_LENGTH = 14;
    private static final int NICKNAME_MIN_NAME_LENGTH = 2;

    @Column(nullable = false, length = NICKNAME_MAX_NAME_LENGTH)
    private String value;

    public Nickname(String value) {
        validateNickname(value);
        this.value = value;
    }

//    public Nickname(String value, UserRepository userRepository) {
//        validateNickname(value);
//        checkDuplicateNickname(value, userRepository);
//        this.value = value;
//    }

    private void validateNickname(String nickname) {
        int nicknameLength = nickname.length();
        if (nicknameLength < NICKNAME_MIN_NAME_LENGTH || nicknameLength > NICKNAME_MAX_NAME_LENGTH) {
            throw new MockBitException(
                    MockbitErrorCode.USER_NICKNAME_LENGTH_INVALID,
                    NICKNAME_MIN_NAME_LENGTH, NICKNAME_MAX_NAME_LENGTH
            );
        }
    }

//    private void checkDuplicateNickname(String nickname, UserRepository userRepository) {
//        if (userRepository.findByNickname(nickname).isPresent()) {
//            throw new MockBitException(MockbitErrorCode.USER_NICKNAME_ALREADY_EXIST);
//        }
//    }
}
