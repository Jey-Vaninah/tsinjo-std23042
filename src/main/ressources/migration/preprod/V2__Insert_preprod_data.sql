INSERT INTO donor (email, full_name) VALUES ('preprod.donor@example.com', 'Preprod Donor');
INSERT INTO beneficiary (email, full_name) VALUES ('preprod.beneficiary@example.com', 'Preprod Beneficiary');
INSERT INTO payment (id, amount, payment_method, payment_date, status)
VALUES ('preprod-payment-1', 100.0, 'CREDIT_CARD', '2025-08-01 10:00:00', 'SUCCEEDED');
INSERT INTO donation (id, donor_email, payment_id, donation_date)
VALUES ('preprod-donation-1', 'preprod.donor@example.com', 'preprod-payment-1', '2025-08-01 10:00:00');
INSERT INTO help (id, beneficiary_email, payment_id, accident_description, help_date)
VALUES ('preprod-help-1', 'preprod.beneficiary@example.com', 'preprod-payment-1', 'Medical emergency', '2025-08-01 12:00:00');
