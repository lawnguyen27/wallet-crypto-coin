package vn.sparkminds.template.controller.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.sparkminds.template.job.KafkaService;
import vn.sparkminds.template.service.FireblocksService;
import vn.sparkminds.template.service.dto.request.TransactionWebhookRequest;

@RestController
@RequestMapping("/api/common/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionCommonController {
    private final FireblocksService fireblocksService;
    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;

    @PostMapping("")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String requestBody,
            @RequestHeader(value = "fireblocks-signature") String signature
    ) throws JsonProcessingException {

        log.info("Received Fireblocks Webhook: {}", requestBody);
        log.info("Received Signature: {}", signature);

        if (!fireblocksService.verifySignature(requestBody, signature)) {
            log.warn("Invalid Fireblocks Signature!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }

        TransactionWebhookRequest request = objectMapper.readValue(requestBody, TransactionWebhookRequest.class);
        kafkaService.sendTransaction(requestBody, request.getData().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    public ResponseEntity<String> handleWebhook(@RequestBody String requestBody) throws JsonProcessingException {
        log.info("Received test request: {}", requestBody);
        TransactionWebhookRequest request = objectMapper.readValue(requestBody, TransactionWebhookRequest.class);
        kafkaService.sendTransaction(requestBody, request.getData().getId());
        return ResponseEntity.noContent().build();
    }
}

