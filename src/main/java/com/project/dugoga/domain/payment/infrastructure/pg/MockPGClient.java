package com.project.dugoga.domain.payment.infrastructure.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dugoga.domain.payment.application.dto.PGPaymentDto;
import com.project.dugoga.domain.payment.application.pg.PGClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MockPGClient implements PGClient {

    private final ObjectMapper objectMapper;

    @Override
    public PGPaymentDto confirm(String paymentKey, UUID orderId, Integer amount) {
        String path = paymentKey.startsWith("fail")
                ? "mock/pg/pg-confirm-fail.json"
                : "mock/pg/pg-confirm-success.json";

        return readJson(path);
    }

    @Override
    public PGPaymentDto cancel(String paymentKey, String reason) {
        String path = "mock/pg/pg-cancel-success.json";
        return readJson(path);
    }

    private PGPaymentDto readJson(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return objectMapper.readValue(resource.getInputStream(), PGPaymentDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Mock JSON load failed", e);
        }
    }
}
