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
    @AttributeOverride(name = "value", column = @Column(name = "userid"))
    private Userid userid;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password"))
    private Password password;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname"))
    private Nickname nickname;

    public User(String userid, String password, String nickname) {
        this.userid = new Userid(userid);
        this.password = new Password(password);
        this.nickname = new Nickname(nickname);
    }
}
