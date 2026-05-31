package com.example.wallet.consumer;

import com.example.wallet.config.RabbitMQConfig;
import com.example.wallet.DTO.HoldRequest;
import com.example.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletRabbitConsumer {

    private final WalletService walletService;

    @RabbitListener(queues = RabbitMQConfig.HOLD_QUEUE)
    public void handleHold(HoldRequest request) {
        log.info("Received hold request: userId={}, auctId={}, amount={}, key={}",
                request.getUserId(), request.getAuctId(), request.getAmount(), request.getIdempotencyKey());

        try {
            walletService.holdBalance(
                    request.getUserId(),
                    request.getAmount(),
                    request.getAuctId(),
                    request.getIdempotencyKey()
            );
        } catch (RuntimeException e) {
            log.error("Hold failed for userId={}, auctId={}: {}",
                    request.getUserId(), request.getAuctId(), e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.RELEASE_QUEUE)
    public void handleRelease(HoldRequest request) {
        log.info("Received release request: userId={}, auctId={}, amount={}, key={}",
                request.getUserId(), request.getAuctId(), request.getAmount(), request.getIdempotencyKey());

        try {
            walletService.releaseBalance(
                    request.getUserId(),
                    request.getAmount(),
                    request.getAuctId(),
                    request.getIdempotencyKey()
            );
        } catch (RuntimeException e) {
            log.error("Release failed for userId={}, auctId={}: {}",
                    request.getUserId(), request.getAuctId(), e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CONVERT_QUEUE)
    public void handleConvert(HoldRequest request) {
        log.info("Received convert request: userId={}, auctId={}, amount={}, key={}",
                request.getUserId(), request.getAuctId(), request.getAmount(), request.getIdempotencyKey());

        try {
            walletService.convertToPayment(
                    request.getUserId(),
                    request.getAmount(),
                    request.getAuctId(),
                    request.getIdempotencyKey()
            );
        } catch (RuntimeException e) {
            log.error("Convert failed for userId={}, auctId={}: {}",
                    request.getUserId(), request.getAuctId(), e.getMessage());
        }
    }
}
