package vn.sparkminds.template.service.mapper;

import org.springframework.stereotype.Service;
import vn.sparkminds.template.entity.Coin;
import vn.sparkminds.template.entity.Network;
import vn.sparkminds.template.entity.Transaction;
import vn.sparkminds.template.entity.Wallet;
import vn.sparkminds.template.service.dto.TransactionDto;

@Service
public class TransactionMapper {

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .hashId(entity.getHashId())
                .fromAddress(entity.getFromAddress())
                .toAddress(entity.getToAddress())
                .status(entity.getStatus())
                .timestamp(entity.getTimestamp())
                .systemProcessDate(entity.getSystemProcessDate())
                .systemConfirmDate(entity.getSystemConfirmDate())
                .externalId(entity.getExternalId())
                .fireblockTransactionId(entity.getFireblockTransactionId())
                .type(entity.getType())
                .coinId(entity.getCoin() != null ? entity.getCoin().getId() : null)
                .networkId(entity.getNetwork() != null ? entity.getNetwork().getId() : null)
                .build();
    }

    public Transaction toEntity(TransactionDto dto, Wallet wallet, Coin coin, Network network) {
        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .hashId(dto.getHashId())
                .fromAddress(dto.getFromAddress())
                .toAddress(dto.getToAddress())
                .status(dto.getStatus())
                .timestamp(dto.getTimestamp())
                .systemProcessDate(dto.getSystemProcessDate())
                .systemConfirmDate(dto.getSystemConfirmDate())
                .externalId(dto.getExternalId())
                .fireblockTransactionId(dto.getFireblockTransactionId())
                .type(dto.getType())
                .wallet(wallet)
                .coin(coin)
                .network(network)
                .build();
    }
}
