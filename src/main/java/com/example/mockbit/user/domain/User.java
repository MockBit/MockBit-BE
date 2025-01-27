package com.example.mockbit.user.domain;

import com.example.mockbit.common.domain.BaseEntity;
import com.example.mockbit.event.domain.event.Password;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "userid", nullable = false, unique = true))
    private Userid userid;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false, unique = true))
    private Nickname nickname;

    public User(String userid, String password, String nickname) {
        this.userid = new Userid(userid);
        this.password = new Password(password);
        this.nickname = new Nickname(nickname);
    }

    public static User createUser(String userid, String password, String nickname) {
        return new User(userid, password, nickname);
    }

    public void changeNickname(String nickname) {
        this.nickname = new Nickname(nickname);
    }

    public void changePassword(String rawPassword) {
        this.password = new Password(rawPassword);
    }

    public boolean isPasswordMismatch(String rawPassword) {
        return !password.matches(rawPassword);
    }
}