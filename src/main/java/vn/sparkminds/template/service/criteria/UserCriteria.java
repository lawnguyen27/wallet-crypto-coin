package vn.sparkminds.template.service.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sparkminds.template.config.jpa.specifications.Criteria;
import vn.sparkminds.template.config.jpa.specifications.filter.Filter;
import vn.sparkminds.template.config.jpa.specifications.filter.LongFilter;
import vn.sparkminds.template.config.jpa.specifications.filter.StringFilter;
import vn.sparkminds.template.entity.enumeration.TransactionStatus;
import vn.sparkminds.template.entity.enumeration.UserRole;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria implements Serializable, Criteria {

    private LongFilter id;
    private StringFilter email;
    private StringFilter name;
    private UserRoleFilter role;
    private StringFilter vaultId;

    public UserCriteria(UserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.vaultId = other.vaultId == null ? null : other.vaultId.copy();
    }

    @Override
    public UserCriteria copy() {
        return new UserCriteria(this);
    }


    @NoArgsConstructor
    public static class UserRoleFilter extends Filter<UserRole> {

        public UserRoleFilter(UserCriteria.UserRoleFilter other) {
            super(other);
        }

        @Override
        public UserCriteria.UserRoleFilter copy() {
            return new UserCriteria.UserRoleFilter(this);
        }

    }
}

