package vn.sparkminds.template.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.sparkminds.template.service.criteria.WalletCriteria;
import vn.sparkminds.template.service.dto.WalletDto;

import java.util.List;
import java.util.Optional;

public interface WalletQueryService {

    Page<WalletDto> getWallets(WalletCriteria criteria, Pageable pageable);

    Page<WalletDto> getWallets(WalletCriteria criteria, Pageable pageable, String email);

    List<WalletDto> getWallets(WalletCriteria criteria);

    Optional<WalletDto> getWallet(Long id);
}
