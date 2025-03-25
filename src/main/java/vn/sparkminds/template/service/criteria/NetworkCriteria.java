package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.BooleanFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.LongFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.StringFilter;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkCriteria implements Serializable, Criteria {

    private LongFilter id;
    private StringFilter name;
    private StringFilter address;
    private LongFilter walletId;
    private StringFilter networkType;

    public NetworkCriteria(NetworkCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.walletId = other.walletId == null ? null : other.walletId.copy();
        this.networkType = other.networkType ==  null ? null : other.networkType.copy();
    }

    @Override
    public NetworkCriteria copy() {
        return new NetworkCriteria(this);
    }
}

