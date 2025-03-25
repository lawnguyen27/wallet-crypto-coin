package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.BigDecimalFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.LongFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.StringFilter;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinCriteria implements Serializable, Criteria {

    private LongFilter id;
    private StringFilter symbol;
    private StringFilter name;
    private StringFilter logoUrl;
    private BigDecimalFilter balance;

    public CoinCriteria(CoinCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.symbol = other.symbol == null ? null : other.symbol.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.logoUrl = other.logoUrl == null ? null : other.logoUrl.copy();
        this.balance = other.balance == null ? null : other.balance.copy();
    }

    @Override
    public CoinCriteria copy() {
        return new CoinCriteria(this);
    }
}

