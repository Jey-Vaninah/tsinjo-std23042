package com.my.tsinjo.repository;

import com.my.tsinjo.domain.Payment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class PaymentDao {
  private final JdbcTemplate jdbcTemplate;

  public Payment save(Payment payment) {
    try {
      String sql =
          "INSERT INTO payment (id, amount, payment_method, payment_date, status) VALUES (?, ?, ?,"
              + " ?, ?) ON CONFLICT (id) DO UPDATE SET amount = EXCLUDED.amount, payment_method ="
              + " EXCLUDED.payment_method, payment_date = EXCLUDED.payment_date, status ="
              + " EXCLUDED.status";
      jdbcTemplate.update(
          sql,
          payment.getId(),
          payment.getAmount(),
          payment.getPaymentMethod(),
          payment.getPaymentDate(),
          payment.getStatus());
      log.info("Saved payment with id: {}", payment.getId());
      return payment;
    } catch (Exception e) {
      log.error("Failed to save payment with id: {}", payment.getId(), e);
      throw e;
    }
  }

  public Optional<Payment> findById(String id) {
    try {
      String sql =
          "SELECT id, amount, payment_method, payment_date, status FROM payment WHERE id = ?";
      return Optional.of(
          jdbcTemplate.queryForObject(sql, new Object[] {id}, this::mapRowToPayment));
    } catch (Exception e) {
      log.warn("Payment not found for id: {}", id);
      return Optional.empty();
    }
  }

  public List<Payment> findByStatus(String status) {
    try {
      String sql =
          "SELECT id, amount, payment_method, payment_date, status FROM payment WHERE status = ?";
      return jdbcTemplate.query(sql, new Object[] {status}, this::mapRowToPayment);
    } catch (Exception e) {
      log.error("Failed to retrieve payments with status: {}", status, e);
      throw e;
    }
  }

  private Payment mapRowToPayment(ResultSet rs, int rowNum) throws SQLException {
    return Payment.builder()
        .id(rs.getString("id"))
        .amount(rs.getDouble("amount"))
        .paymentMethod(rs.getString("payment_method"))
        .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
        .status(rs.getString("status"))
        .build();
  }
}
