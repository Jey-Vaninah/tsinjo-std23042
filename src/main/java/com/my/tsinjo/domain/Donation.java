package com.my.tsinjo.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Donation {
  private String id;
  private Donor donor;
  private Payment payment;
  private LocalDateTime donationDate;
}
