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
public class Help {
  private String id;
  private Beneficiary beneficiary;
  private Payment payment;
  private String accidentDescription;
  private LocalDateTime helpDate;
}
