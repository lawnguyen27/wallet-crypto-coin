package vn.sparkminds.template.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.sparkminds.template.config.jpa.AbstractAuditingEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "m_limit_tokens")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LimitToken extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false, precision = 38, scale = 18)
    private BigDecimal limitQuantity = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "fk_coin_id", nullable = false)
    private Coin coin;
}
