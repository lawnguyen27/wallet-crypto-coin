package vn.sparkminds.template.controller.publics;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.sparkminds.template.service.TransactionQueryService;
import vn.sparkminds.template.service.TransactionService;
import vn.sparkminds.template.service.dto.request.CreateTransactionRequestDto;
import vn.sparkminds.template.service.dto.request.CreateTransactionWithdrawRequestDto;

@RestController
@RequestMapping("/api/public/v1/transactions")
@RequiredArgsConstructor
public class TransactionPublicController {

    private final TransactionService transactionService;
    private final TransactionQueryService transactionQueryService;

    @PostMapping("")
    public ResponseEntity<CreateTransactionRequestDto> createTransaction
            (@Valid @RequestBody CreateTransactionWithdrawRequestDto request, Authentication authentication) {

        transactionService.createTransactionWithdraw(request, authentication.getName(), null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CreateTransactionRequestDto> createTransactionWithdraw
            (@Valid @RequestBody CreateTransactionWithdrawRequestDto request, Authentication authentication) {

        transactionService.createTransactionWithdraw(request, authentication.getName(), null);
        return ResponseEntity.ok().build();
    }
}

