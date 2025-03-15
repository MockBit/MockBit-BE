package com.example.mockbit.account.domain.repository;

import com.example.mockbit.account.domain.Btc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BtcRepository extends JpaRepository<Btc, Long> {
    Btc findByUserId(Long userId);

    @Query("SELECT b.user.id FROM Btc b WHERE b.btcBalance > 0")
    List<Long> findAllUserIdsWithBtcBalance();
}
