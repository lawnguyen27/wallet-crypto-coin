package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.BigDecimalFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.Filter;
import vn.sparkminds.template.config.jpa.specifications.filter.InstantFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.LongFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.StringFilter;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.entity.enumeration.TransactionType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCriteria implements Serializable, Criteria {

    private LongFilter id;
    private LongFilter coinId;
    private LongFilter networkId;
    private LongFilter walletId;
    private BigDecimalFilter amount;
    private StringFilter fromAddress;
    private StringFilter toAddress;
    private TransactionStatusFilter status;
    private StringFilter hashId;
    private StringFilter fireblockTransactionId;
    private StringFilter externalId;
    private TransactionTypeFilter type;
    private InstantFilter timestamp;
    private InstantFilter systemProcessDate;
    private InstantFilter systemConfirmDate;

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.coinId = other.coinId == null ? null : other.coinId.copy();
        this.networkId = other.networkId == null ? null : other.networkId.copy();
        this.walletId = other.walletId == null ? null : other.walletId.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.fromAddress = other.fromAddress == null ? null : other.fromAddress.copy();
        this.toAddress = other.toAddress == null ? null : other.toAddress.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.hashId = other.hashId == null ? null : other.hashId.copy();
        this.fireblockTransactionId = other.fireblockTransactionId == null ? null : other.fireblockTransactionId.copy();
        this.externalId = other.externalId == null ? null : other.externalId.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.timestamp = other.timestamp == null ? null : other.timestamp.copy();
        this.systemProcessDate = other.systemProcessDate == null ? null : other.systemProcessDate.copy();
        this.systemConfirmDate = other.systemConfirmDate == null ? null : other.systemConfirmDate.copy();
    }

    @Override
    public Criteria copy() {
        return new TransactionCriteria(this);
    }

    @NoArgsConstructor
    public static class TransactionStatusFilter extends Filter<TransactionStatus> {
        public TransactionStatusFilter(TransactionStatusFilter other) {
            super(other);
        }
        @Override
        public TransactionStatusFilter copy() {
            return new TransactionStatusFilter(this);
        }
    }

    @NoArgsConstructor
    public static class TransactionTypeFilter extends Filter<TransactionType> {
        public TransactionTypeFilter(TransactionTypeFilter other) {
            super(other);
        }
        @Override
        public TransactionTypeFilter copy() {
            return new TransactionTypeFilter(this);
        }
    }
}
