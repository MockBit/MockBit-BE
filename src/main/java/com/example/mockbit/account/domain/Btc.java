package com.example.mockbit.account.domain;

import com.example.mockbit.common.domain.BaseEntity;
import com.example.mockbit.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "btcs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Btc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal btcBalance;

    public Btc(User user) {
        this.user = user;
        this.btcBalance = BigDecimal.ZERO;
    }

    public void updateBtcBalance(BigDecimal btcAmount) {
        this.btcBalance = btcBalance.add(btcAmount);
    }
}
