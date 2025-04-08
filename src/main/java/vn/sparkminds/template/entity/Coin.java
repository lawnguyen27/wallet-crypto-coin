package vn.sparkminds.template.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.sparkminds.template.config.jpa.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "m_coins")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coin extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    @Column(name="symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name="logo_url")
    private String logoUrl;

    @Column(name="price")
    private BigDecimal price = BigDecimal.ZERO;

    @ManyToMany
    @JoinTable(
            name = "m_coin_networks",
            joinColumns = @JoinColumn(name = "fk_coin_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_network_id")
    )
    private List<Network> networks;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;


}
