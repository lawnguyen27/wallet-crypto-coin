package vn.sparkminds.template.service;

import vn.sparkminds.template.service.dto.LimitTokenDto;
import vn.sparkminds.template.service.dto.request.LimitTokenUpdateRequestDto;

public interface LimitTokenService {

    void updateLimitToken(Long coinId, LimitTokenUpdateRequestDto request);
}
