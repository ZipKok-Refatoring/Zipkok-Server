package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TransactionPriceConfig")
@NoArgsConstructor
@Getter
public class TransactionPriceConfig {

    @Id
    @Column(name = "transaction_config_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionConfigId;

    @Column(name = "mprice_min")
    private long mPriceMin;

    @Column(name = "mprice_max")
    private long mPriceMax;

    @Column(name = "mdeposit_min")
    private long mDepositMin;

    @Column(name = "mdeposit_max")
    private long mDepositMax;

    @Column(name = "ydeposit_min")
    private long yDepositMin;

    @Column(name = "ydeposit_max")
    private long yDepositMax;

    @Column(name = "purchase_min")
    private long purchaseMin;

    @Column(name = "purchase_max")
    private long puchaseMax;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TransactionPriceConfig(User user) {
        this.user = user;
        this.status = "active";
    }
}
