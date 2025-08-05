package com.my.tsinjo.repository;

import com.my.tsinjo.domain.Donation;
import com.my.tsinjo.domain.Donor;
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
public class DonationDao {

  private final JdbcTemplate jdbcTemplate;

  public Optional<Donation> findById(String id) {
    log.info("Finding donation by id: {}", id);
    try {
      String sql = "SELECT id, donor_email, payment_id, donation_date FROM donation WHERE id = ?";
      Donation donation = jdbcTemplate.queryForObject(sql, this::mapRowToDonation, id);
      return Optional.ofNullable(donation);
    } catch (Exception e) {
      log.error("Failed to find donation by id: {}", id, e);
      return Optional.empty();
    }
  }

  public Donation save(Donation donation) {
    log.info("Saving donation with id: {}", donation.getId());
    try {
      String sql =
          """
          INSERT INTO donation (id, donor_email, payment_id, donation_date)
          VALUES (?, ?, ?, ?)
          ON CONFLICT (id) DO UPDATE SET
              donor_email = EXCLUDED.donor_email,
              payment_id = EXCLUDED.payment_id,
              donation_date = EXCLUDED.donation_date
          RETURNING *""";
      Donation saved =
          jdbcTemplate.queryForObject(
              sql,
              this::mapRowToDonation,
              donation.getId(),
              donation.getDonor().getEmail(),
              donation.getPayment().getId(),
              Timestamp.valueOf(donation.getDonationDate()));
      log.info("Donation saved with id: {}", donation.getId());
      return saved;
    } catch (Exception e) {
      log.error("Failed to save donation with id: {}", donation.getId(), e);
      throw e;
    }
  }

  private Donation mapRowToDonation(ResultSet rs, int rowNum) throws SQLException {
    return Donation.builder()
        .id(rs.getString("id"))
        .donor(Donor.builder().email(rs.getString("donor_email")).build())
        .payment(Payment.builder().id(rs.getString("payment_id")).build())
        .donationDate(rs.getTimestamp("donation_date").toLocalDateTime())
        .build();
  }

  public List<Donation> findAllByOrderByDonationDateDesc() {
    String sql =
        "SELECT id, donor_email, payment_id, donation_date FROM donation ORDER BY donation_date"
            + " DESC";
    return jdbcTemplate.query(sql, this::mapRowToDonation);
  }
}
