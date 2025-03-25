package vn.sparkminds.template.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import vn.sparkminds.template.service.TransactionService;
import vn.sparkminds.template.service.dto.request.TransactionWebhookRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public void sendTransaction(String transactionData, String fireblockId) {
        log.info("Send transaction as message {}", transactionData);
        ProducerRecord<String, String> record = new ProducerRecord<>("transaction_topic", fireblockId, transactionData);
        kafkaTemplate.send(record);
    }

    @KafkaListener(topics = "transaction_topic", groupId = "transaction_group")
    public void consumeTransaction(@Header(KafkaHeaders.RECEIVED_KEY) String fireblockId,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Payload String message) {
        try {
            TransactionWebhookRequest request = objectMapper.readValue(message, TransactionWebhookRequest.class);
            log.info("Received transaction: fireblockId={}, partition={}, message={}", fireblockId, partition, request);

            transactionService.processTransaction(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
