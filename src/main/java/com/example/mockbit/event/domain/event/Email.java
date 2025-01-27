package com.example.mockbit.event.domain.event;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Email {

    private static final int EMAIL_CODE_LENGTH = 6;
    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"
    );

    private String value;
    private String verificationCode;

    public Email(String email) {
        validateEmailAddress(email);
        this.value = email;
        this.verificationCode = generateVerficationCode();
    }

    private void validateEmailAddress(String email) {
        if (email == null) {
            throw new MockBitException(MockbitErrorCode.USER_EMAIL_ADDRESS_EMPTY);
        }

        Matcher matcher = EMAIL_ADDRESS_PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new MockBitException(MockbitErrorCode.USER_EMAIL_ADDRESS_FORMAT_INVALID);
        }
    }

    private String generateVerficationCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < EMAIL_CODE_LENGTH; i++) {
            codeBuilder.append(random.nextInt(10));
        }
        return codeBuilder.toString();
    }

    public void sendVerfication() {

    }

    public boolean verifyCode(String inputCode) {
        if (verificationCode == null || !verificationCode.equals(inputCode)) {
            throw new MockBitException(MockbitErrorCode.USER_EMAIL_CODE_NOT_EQUAL);
        }
        return true;
    }

    public void resetVerficationCode() {
        this.verificationCode = generateVerficationCode();
    }
}
