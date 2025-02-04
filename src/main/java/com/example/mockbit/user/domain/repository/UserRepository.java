package com.example.mockbit.user.domain.repository;

import com.example.mockbit.user.domain.Nickname;
import com.example.mockbit.user.domain.User;
import com.example.mockbit.user.domain.Userid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserid(Userid userid);
    Optional<User> findByNickname(Nickname nickname);
}
