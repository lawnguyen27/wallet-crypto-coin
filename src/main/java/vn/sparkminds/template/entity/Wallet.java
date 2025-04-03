package vn.sparkminds.template.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.sparkminds.template.config.jpa.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_wallets")
public class Wallet extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "fk_coin_id", nullable = false)
    private Coin coin;

    @ManyToOne
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "fk_network_id", nullable = false)
    private Network network;
}
