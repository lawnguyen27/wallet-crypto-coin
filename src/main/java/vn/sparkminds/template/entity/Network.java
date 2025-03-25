package vn.sparkminds.template.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.sparkminds.template.config.jpa.AbstractAuditingEntity;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_networks")
public class Network extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String networkType;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;

    @ManyToMany(mappedBy = "networks")
    private List<Coin> coins;

    @OneToMany(mappedBy = "network", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wallet> wallets;

}
