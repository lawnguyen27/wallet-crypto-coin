package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletCriteria implements Serializable, Criteria {

    private LongFilter id;
    private LongFilter userId;
    private LongFilter coinId;
    private LongFilter networkId;
    private BigDecimalFilter balance;
    private StringFilter address;

    public WalletCriteria(WalletCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.coinId = other.coinId == null ? null : other.coinId.copy();
        this.balance = other.balance == null ? null : other.balance.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.networkId = other.networkId == null ? null : other.networkId.copy();
    }

    @Override
    public WalletCriteria copy() {
        return new WalletCriteria(this);
    }
}

