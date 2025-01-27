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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Userid {

    public static final int ID_MAX_NAME_LENGTH = 14;
    private static final int ID_MIN_NAME_LENGTH = 8;

    @Column(nullable = false, length = ID_MAX_NAME_LENGTH)
    private String value;

    public Userid(String value) {
        validateUserid(value);
        this.value = value;
    }

    private void validateUserid(String userid) {
        int useridLength = userid.length();
        if (useridLength < ID_MIN_NAME_LENGTH || useridLength > ID_MAX_NAME_LENGTH) {
            throw new MockBitException(
                    MockbitErrorCode.USER_ID_LENGTH_INVALID,
                    ID_MIN_NAME_LENGTH, ID_MAX_NAME_LENGTH
            );
        }
    }
}
