package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.NetworkCriteria;
import vn.sparkminds.template.service.dto.NetworkDto;

import java.util.List;
import java.util.Optional;

public interface NetworkQueryService {
    Page<NetworkDto> getNetworks(NetworkCriteria criteria, Pageable pageable);

    List<NetworkDto> getNetworks(NetworkCriteria criteria);

}
