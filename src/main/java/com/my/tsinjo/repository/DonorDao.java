package com.my.tsinjo.repository;

import com.my.tsinjo.domain.Donor;
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
public class DonorDao {
  private final JdbcTemplate jdbcTemplate;

  public Optional<Donor> findById(String email) {
    log.info("Finding donor by email: {}", email);
    try {
      String sql = "SELECT email, full_name FROM donor WHERE email = ?";
      Donor donor = jdbcTemplate.queryForObject(sql, this::mapRowToDonor, email);
      return Optional.ofNullable(donor);
    } catch (Exception e) {
      log.error("Failed to find donor by email: {}", email, e);
      return Optional.empty();
    }
  }

  public Donor save(Donor donor) {
    log.info("Saving donor: {}", donor.getEmail());
    try {
      String sql =
          "INSERT INTO donor (email, full_name) VALUES (?, ?) ON CONFLICT (email) DO UPDATE SET"
              + " full_name = EXCLUDED.full_name RETURNING *";
      Donor saved =
          jdbcTemplate.queryForObject(
              sql, this::mapRowToDonor, donor.getEmail(), donor.getFullName());
      log.info("Donor saved: {}", donor.getEmail());
      return saved;
    } catch (Exception e) {
      log.error("Failed to save donor: {}", donor.getEmail(), e);
      throw e;
    }
  }

  private Donor mapRowToDonor(ResultSet rs, int rowNum) throws SQLException {
    return Donor.builder().email(rs.getString("email")).fullName(rs.getString("full_name")).build();
  }
}
