package com.example.mockbit.account.domain;

import com.example.mockbit.common.domain.BaseEntity;
import com.example.mockbit.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal avgEntryPrice;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal avgLeverage;

    @Column(nullable = false, length = 10)
    private String position;

    @Column(nullable = true, precision = 18, scale = 8)
    private BigDecimal liquidationPrice;

    public Btc(User user) {
        this.user = user;
        this.btcBalance = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.avgEntryPrice = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.avgLeverage = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.position = "NONE";
        this.liquidationPrice = null;
    }

    public void updateBtcBalance(BigDecimal btcAmount) {
        if (btcAmount == null) {
            throw new IllegalArgumentException("BTC 추가량이 null일 수 없습니다.");
        }

        BigDecimal newBalance = btcBalance.add(btcAmount).setScale(8, RoundingMode.HALF_UP);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("BTC 잔액은 0 미만이 될 수 없습니다.");
        }

        this.btcBalance = newBalance;
    }

    public void updatePosition(String position) {
        this.position = position;
    }

    public void updateAvgEntryPrice(BigDecimal avgEntryPrice) {
        this.avgEntryPrice = avgEntryPrice;
    }

    public void updateAvgLeverage(BigDecimal avgLeverage) {
        this.avgLeverage = avgLeverage;
    }

    public void updateLiquidationPrice() {
        if (this.btcBalance.compareTo(BigDecimal.ZERO) > 0 && this.avgLeverage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal one = BigDecimal.ONE;
            BigDecimal leverageFactor = one.divide(this.avgLeverage, 8, RoundingMode.HALF_UP);

            if ("LONG".equalsIgnoreCase(this.position)) {
                this.liquidationPrice = this.avgEntryPrice.multiply(one.subtract(leverageFactor))
                        .setScale(8, RoundingMode.HALF_UP);
            } else if ("SHORT".equalsIgnoreCase(this.position)) {
                this.liquidationPrice = this.avgEntryPrice.multiply(one.add(leverageFactor))
                        .setScale(8, RoundingMode.HALF_UP);
            } else {
                this.liquidationPrice = null;
            }
        } else {
            this.liquidationPrice = null;
        }
    }

    public void executeLiquidation() {
        this.btcBalance = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.avgEntryPrice = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.avgLeverage = BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        this.position = "NONE";
        this.liquidationPrice = null;
    }

    public void resetLiquidationPrice() {
        this.liquidationPrice = null;
    }

    public boolean isBtcEnough(BigDecimal amount) {
        return btcBalance.compareTo(amount) >= 0;
    }
}
