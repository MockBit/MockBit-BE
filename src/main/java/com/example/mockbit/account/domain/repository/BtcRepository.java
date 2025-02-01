package com.example.mockbit.account.domain.repository;

import com.example.mockbit.account.domain.Btc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BtcRepository extends JpaRepository<Btc, Long> {
    Btc findByUserId(Long userId);
}
