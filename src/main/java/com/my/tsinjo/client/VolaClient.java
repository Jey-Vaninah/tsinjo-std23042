package com.my.tsinjo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class VolaClient {
  private final RestTemplate restTemplate;
  private final String apiKey;
  private static final String VOLA_API_URL =
      "https://42cwka3n4ifcp7ufheyrpmph240iuaxo.lambda-url.eu-west-3.on.aws/v3/payments/";

  public VolaClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.apiKey = System.getenv("VOLA_API_KEY");
    if (apiKey == null || apiKey.isBlank()) {
      throw new RuntimeException("Clé API Vola non définie");
    }
  }

  public String submitPayment(double amount, String paymentMethod) {
    log.info("Submitting payment: amount={}, method={}", amount, paymentMethod);
    try {
      String url = VOLA_API_URL + "?apiKey=" + apiKey;
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");
      String payload =
          String.format("{\"amount\": %f, \"paymentMethod\": \"%s\"}", amount, paymentMethod);
      HttpEntity<String> request = new HttpEntity<>(payload, headers);
      String paymentId = restTemplate.postForObject(url, request, String.class);
      log.info("Payment submitted with id: {}", paymentId);
      return paymentId;
    } catch (Exception e) {
      log.error("Failed to submit payment: amount={}, method={}", amount, paymentMethod, e);
      throw e;
    }
  }

  public String checkPaymentStatus(String paymentId) {
    log.info("Checking payment status for id: {}", paymentId);
    try {
      String url = VOLA_API_URL + paymentId + "?apiKey=" + apiKey;
      String status = restTemplate.getForObject(url, String.class);
      log.info("Payment {} status: {}", paymentId, status);
      return status;
    } catch (Exception e) {
      log.error("Failed to check payment status for id: {}", paymentId, e);
      throw e;
    }
  }
}
