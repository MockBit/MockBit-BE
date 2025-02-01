package com.example.mockbit.account.domain;

import com.example.mockbit.common.domain.BaseEntity;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal balance;

    public Account(User user, BigDecimal initialBalance) {
        this.user = user;
        this.balance = initialBalance;
    }

    public void deductBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void refundBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
