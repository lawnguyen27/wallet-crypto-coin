package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.BigDecimalFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.LongFilter;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitTokenCriteria implements Serializable, Criteria {

    private LongFilter id;
    private BigDecimalFilter limitQuantity;
    private LongFilter coinId;

    public LimitTokenCriteria(LimitTokenCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.limitQuantity = other.limitQuantity == null ? null : other.limitQuantity.copy();
        this.coinId = other.coinId == null ? null : other.coinId.copy();
    }

    @Override
    public LimitTokenCriteria copy() {
        return new LimitTokenCriteria(this);
    }
}