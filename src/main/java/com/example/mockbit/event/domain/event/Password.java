package com.example.mockbit.event.domain.event;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Password {

    public static final int PASSWORD_MAX_NAME_LENGTH = 14;
    private static final int PASSWORD_MIN_NAME_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{" + PASSWORD_MIN_NAME_LENGTH + "," + PASSWORD_MAX_NAME_LENGTH + "}$"
    );
    private static final String HASH_ALGORITHM = "SHA-256";

    private String value;

    public Password(String password) {
        validatePassword(password);
        this.value = encode(password);
    }

    private void validatePassword(String password) {
        int passwordLength = password.length();
        if (passwordLength < PASSWORD_MIN_NAME_LENGTH || passwordLength > PASSWORD_MAX_NAME_LENGTH) {
            throw new MockBitException(
                    MockbitErrorCode.USER_PASSWORD_LENGTH_INVALID,
                    PASSWORD_MIN_NAME_LENGTH, PASSWORD_MAX_NAME_LENGTH
            );
        }

        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        if (!matcher.matches()) {
            throw new MockBitException(MockbitErrorCode.USER_PASSWORD_FORMAT_INVALID);
        }
    }

    private String encode(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedPassword = digest.digest(rawPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("해당 해쉬 알고리즘이 존재하지 않습니다.");
        }
    }

    public boolean matches(String rawPassword) {
        String hashedPassword = encode(rawPassword);
        return value.equals(hashedPassword);
    }
}
