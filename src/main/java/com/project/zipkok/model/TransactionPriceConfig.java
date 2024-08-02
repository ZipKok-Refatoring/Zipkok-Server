package com.project.zipkok.model;

import com.project.zipkok.dto.PatchOnBoardingRequest;
import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TransactionPriceConfig")
@NoArgsConstructor
@Getter
@Setter
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
    private long purchaseMax;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TransactionPriceConfig(User user) {
        this.user = user;
    }

        public void setTransactionPriceConfig(PatchOnBoardingRequest patchOnBoardingRequest) {
        this.mPriceMin = patchOnBoardingRequest.getMpriceMin();
        this.mPriceMax = patchOnBoardingRequest.getMpriceMax();
        this.mDepositMin = patchOnBoardingRequest.getMdepositMin();
        this.mDepositMax = patchOnBoardingRequest.getMdepositMax();
        this.yDepositMin = patchOnBoardingRequest.getYdepositMin();
        this.yDepositMax = patchOnBoardingRequest.getYdepositMax();
        this.purchaseMin = patchOnBoardingRequest.getPurchaseMin();
        this.purchaseMax = patchOnBoardingRequest.getPurchaseMax();
    }
}
