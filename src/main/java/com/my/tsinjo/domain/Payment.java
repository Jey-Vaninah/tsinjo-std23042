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
public class Payment {
  private String id;
  private double amount;
  private String paymentMethod;
  private LocalDateTime paymentDate;
  private String status;
}
