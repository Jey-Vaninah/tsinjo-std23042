package com.my.tsinjo.service.event;

import com.my.tsinjo.client.VolaClient;
import com.my.tsinjo.domain.Payment;
import com.my.tsinjo.endpoint.event.model.PaymentVerificationRequested;
import com.my.tsinjo.repository.PaymentDao;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentVerificationService implements Consumer<PaymentVerificationRequested> {
  private final VolaClient volaClient;
  private final PaymentDao paymentDao;

  @SneakyThrows
  @Override
  public void accept(PaymentVerificationRequested event) {
    log.info("Processing payment verification for paymentId: {}", event.getPaymentId());
    try {
      String status = volaClient.checkPaymentStatus(event.getPaymentId());
      Payment payment =
          paymentDao
              .findById(event.getPaymentId())
              .orElseThrow(
                  () -> new IllegalStateException("Payment not found: " + event.getPaymentId()));
      payment.setStatus(status);
      paymentDao.save(payment);
      log.info("Updated payment {} status to {}", event.getPaymentId(), status);
    } catch (Exception e) {
      log.error("Failed to verify payment {}", event.getPaymentId(), e);
      throw e;
    }
  }

  @Scheduled(fixedRate = 30000)
  public void pollPendingPayments() {
    log.info("Polling for payments in VERIFYING state");
    try {
      paymentDao
          .findByStatus("VERIFYING")
          .forEach(
              payment -> {
                PaymentVerificationRequested event =
                    PaymentVerificationRequested.builder().paymentId(payment.getId()).build();
                accept(event);
              });
    } catch (Exception e) {
      log.error("Failed to poll pending payments", e);
    }
  }
}
