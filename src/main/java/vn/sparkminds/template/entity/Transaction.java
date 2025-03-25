package vn.sparkminds.template.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.sparkminds.template.config.jpa.AbstractAuditingEntity;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.entity.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_transactions")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false)
    private Instant timestamp ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_wallet_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_wallet_id"))
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_coin_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_coin_id"))
    private Coin coin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_network_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_network_id"))
    private Network network;

    @Column(name = "hash_id")
    private String hashId;

    @Column(name = "fireblock_transaction_id", unique = true)
    private String fireblockTransactionId;

    @Column(name = "system_process_date", nullable = false)
    private Instant systemProcessDate;

    @Column(name = "system_confirm_date")
    private Instant systemConfirmDate;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "external_id",unique = true)
    private String externalId;

    @JsonIgnore
    public boolean isPending() {
        return TransactionStatus.COMFIRMING.equals(status) || TransactionStatus.CREATED.equals(status);
    }
}
