package vn.sparkminds.template.controller.privates;

import com.fireblocks.sdk.model.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.sparkminds.template.service.FireblocksTransactionService;
import vn.sparkminds.template.service.TransactionQueryService;
import vn.sparkminds.template.service.criteria.TransactionCriteria;
import vn.sparkminds.template.service.dto.TransactionDto;
import vn.sparkminds.template.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/private/v1/transactions")
@RequiredArgsConstructor
public class TransactionPrivateController {

    private final TransactionQueryService transactionQueryService;
    private final FireblocksTransactionService fireblocksTransactionService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('viewTransactions')")
    public ResponseEntity<List<TransactionDto>> getTransactions(TransactionCriteria criteria, @PageableDefault Pageable pageable) {
        final Page<TransactionDto> page = transactionQueryService.getTransactions(criteria, pageable);
        final HttpHeaders headers = ResponseUtils.generatePaginationHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('viewTransactions')")
    public ResponseEntity<List<TransactionDto>> getTransactions(TransactionCriteria criteria) {
        return ResponseEntity.ok(transactionQueryService.getTransactions(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN') || hasAuthority('viewTransactions')")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
        return ResponseUtils.wrapOrNotFound(transactionQueryService.getTransaction(id));
    }

    @GetMapping("/fireblock")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<TransactionResponse> getTransactionByFireblock(@RequestParam String id) {
        TransactionResponse response = fireblocksTransactionService.getTransactionById(id,false);
        return ResponseEntity.ok(response);
    }
}
