package com.my.tsinjo.endpoint.rest.controller;

import com.my.tsinjo.client.VolaClient;
import com.my.tsinjo.domain.Donation;
import com.my.tsinjo.domain.Donor;
import com.my.tsinjo.domain.Help;
import com.my.tsinjo.domain.Payment;
import com.my.tsinjo.endpoint.event.EventProducer;
import com.my.tsinjo.endpoint.event.model.PaymentVerificationRequested;
import com.my.tsinjo.repository.DonationDao;
import com.my.tsinjo.repository.DonorDao;
import com.my.tsinjo.repository.HelpDao;
import com.my.tsinjo.repository.PaymentDao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/")
@AllArgsConstructor
public class TsinjoController {

  private final DonationDao donationDao;
  private final DonorDao donorDao;
  private final HelpDao helpDao;
  private final PaymentDao paymentDao;
  private final VolaClient volaClient;
  private final EventProducer<PaymentVerificationRequested> eventProducer;

  @GetMapping
  public String index(Model model) {
    try {
      List<Donation> donations = donationDao.findAllByOrderByDonationDateDesc();
      List<Help> helps = helpDao.findAllByOrderByHelpDateDesc();
      model.addAttribute("donations", donations);
      model.addAttribute("helps", helps);
      log.info("Loaded {} donations and {} helps for display", donations.size(), helps.size());
      return "index";
    } catch (Exception e) {
      log.error("Failed to load index page", e);
      throw e;
    }
  }

  @PostMapping("/donate")
  @SneakyThrows
  public String donate(
      @RequestParam String email,
      @RequestParam String fullName,
      @RequestParam double amount,
      @RequestParam String paymentMethod) {
    log.info("Processing donation from {} for amount {}", email, amount);
    try {
      Donor donor =
          donorDao
              .findById(email)
              .orElseGet(
                  () -> donorDao.save(Donor.builder().email(email).fullName(fullName).build()));
      String paymentId = volaClient.submitPayment(amount, paymentMethod);
      Payment payment =
          paymentDao.save(
              Payment.builder()
                  .id(paymentId)
                  .amount(amount)
                  .paymentMethod(paymentMethod)
                  .paymentDate(LocalDateTime.now())
                  .status("VERIFYING")
                  .build());
      donationDao.save(
          Donation.builder()
              .id(UUID.randomUUID().toString())
              .donor(donor)
              .payment(payment)
              .donationDate(LocalDateTime.now())
              .build());
      eventProducer.accept(
          List.of(PaymentVerificationRequested.builder().paymentId(paymentId).build()));
      log.info("Donation submitted with paymentId: {}", paymentId);
      return "redirect:/";
    } catch (Exception e) {
      log.error("Failed to process donation for email: {}", email, e);
      throw e;
    }
  }
}
