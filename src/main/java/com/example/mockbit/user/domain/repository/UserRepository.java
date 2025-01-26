package com.example.mockbit.user.domain.repository;

import com.example.mockbit.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserid(String userid);
    Optional<User> findByNickname(String nickname);
}
