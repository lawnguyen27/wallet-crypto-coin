package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.LimitTokenCriteria;
import vn.sparkminds.template.service.criteria.UserCriteria;
import vn.sparkminds.template.service.dto.LimitTokenDto;
import vn.sparkminds.template.service.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface LimitTokenQueryService {

    Page<LimitTokenDto> getLimitTokens(LimitTokenCriteria criteria, Pageable pageable);

    List<LimitTokenDto> getLimitTokens(LimitTokenCriteria criteria);

    Optional<LimitTokenDto> getLimitToken(Long id);
}
