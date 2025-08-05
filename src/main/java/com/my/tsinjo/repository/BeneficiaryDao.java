package com.my.tsinjo.repository;

import com.my.tsinjo.domain.Beneficiary;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class BeneficiaryDao {
  private final JdbcTemplate jdbcTemplate;

  public Optional<Beneficiary> findById(String email) {
    log.info("Finding beneficiary by email: {}", email);
    try {
      String sql = "SELECT email, full_name FROM beneficiary WHERE email = ?";
      Beneficiary beneficiary = jdbcTemplate.queryForObject(sql, this::mapRowToBeneficiary, email);
      return Optional.ofNullable(beneficiary);
    } catch (Exception e) {
      log.error("Failed to find beneficiary by email: {}", email, e);
      return Optional.empty();
    }
  }

  public Beneficiary save(Beneficiary beneficiary) {
    log.info("Saving beneficiary: {}", beneficiary.getEmail());
    try {
      String sql =
          "INSERT INTO beneficiary (email, full_name) VALUES (?, ?) ON CONFLICT (email) DO UPDATE"
              + " SET full_name = EXCLUDED.full_name RETURNING *";
      Beneficiary saved =
          jdbcTemplate.queryForObject(
              sql, this::mapRowToBeneficiary, beneficiary.getEmail(), beneficiary.getFullName());
      log.info("Beneficiary saved: {}", beneficiary.getEmail());
      return saved;
    } catch (Exception e) {
      log.error("Failed to save beneficiary: {}", beneficiary.getEmail(), e);
      throw e;
    }
  }

  private Beneficiary mapRowToBeneficiary(ResultSet rs, int rowNum) throws SQLException {
    return Beneficiary.builder()
        .email(rs.getString("email"))
        .fullName(rs.getString("full_name"))
        .build();
  }
}
