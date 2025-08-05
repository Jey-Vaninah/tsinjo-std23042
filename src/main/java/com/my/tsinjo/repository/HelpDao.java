package com.my.tsinjo.repository;

import com.my.tsinjo.domain.Beneficiary;
import com.my.tsinjo.domain.Help;
import com.my.tsinjo.domain.Payment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class HelpDao {

  private final JdbcTemplate jdbcTemplate;

  public Optional<Help> findById(String id) {
    log.info("Finding help by id: {}", id);
    try {
      String sql =
          "SELECT id, beneficiary_email, payment_id, accident_description, help_date FROM help"
              + " WHERE id = ?";
      Help help = jdbcTemplate.queryForObject(sql, this::mapRowToHelp, id);
      return Optional.ofNullable(help);
    } catch (Exception e) {
      log.error("Failed to find help by id: {}", id, e);
      return Optional.empty();
    }
  }

  public Help save(Help help) {
    log.info("Saving help with id: {}", help.getId());
    try {
      String sql =
          """
          INSERT INTO help (id, beneficiary_email, payment_id, accident_description, help_date)
          VALUES (?, ?, ?, ?, ?)
          ON CONFLICT (id) DO UPDATE SET
              beneficiary_email = EXCLUDED.beneficiary_email,
              payment_id = EXCLUDED.payment_id,
              accident_description = EXCLUDED.accident_description,
              help_date = EXCLUDED.help_date
          RETURNING *""";
      Help saved =
          jdbcTemplate.queryForObject(
              sql,
              this::mapRowToHelp,
              help.getId(),
              help.getBeneficiary().getEmail(),
              help.getPayment().getId(),
              help.getAccidentDescription(),
              Timestamp.valueOf(help.getHelpDate()));
      log.info("Help saved with id: {}", help.getId());
      return saved;
    } catch (Exception e) {
      log.error("Failed to save help with id: {}", help.getId(), e);
      throw e;
    }
  }

  private Help mapRowToHelp(ResultSet rs, int rowNum) throws SQLException {
    return Help.builder()
        .id(rs.getString("id"))
        .beneficiary(Beneficiary.builder().email(rs.getString("beneficiary_email")).build())
        .payment(Payment.builder().id(rs.getString("payment_id")).build())
        .accidentDescription(rs.getString("accident_description"))
        .helpDate(rs.getTimestamp("help_date").toLocalDateTime())
        .build();
  }

  public List<Help> findAllByOrderByHelpDateDesc() {
    String sql =
        "SELECT id, beneficiary_email, payment_id, accident_description, help_date FROM help ORDER"
            + " BY help_date DESC";
    return jdbcTemplate.query(sql, this::mapRowToHelp);
  }
}
