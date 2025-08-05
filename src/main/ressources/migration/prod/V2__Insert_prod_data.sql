INSERT INTO donor (email, full_name) VALUES ('prod.donor@example.com', 'Prod Donor');
INSERT INTO beneficiary (email, full_name) VALUES ('prod.beneficiary@example.com', 'Prod Beneficiary');
INSERT INTO payment (id, amount, payment_method, payment_date, status)
VALUES ('prod-payment-1', 200.0, 'BANK_TRANSFER', '2025-08-02 14:00:00', 'SUCCEEDED');
INSERT INTO donation (id, donor_email, payment_id, donation_date)
VALUES ('prod-donation-1', 'prod.donor@example.com', 'prod-payment-1', '2025-08-02 14:00:00');
INSERT INTO help (id, beneficiary_email, payment_id, accident_description, help_date)
VALUES ('prod-help-1', 'prod.beneficiary@example.com', 'prod-payment-1', 'Accident recovery', '2025-08-02 16:00:00');
