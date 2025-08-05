CREATE TABLE donor (
                       email VARCHAR(255) PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL
);

CREATE TABLE beneficiary (
                             email VARCHAR(255) PRIMARY KEY,
                             full_name VARCHAR(255) NOT NULL
);

CREATE TABLE payment (
                         id VARCHAR(255) PRIMARY KEY,
                         amount DOUBLE PRECISION NOT NULL,
                         payment_method VARCHAR(255) NOT NULL,
                         payment_date TIMESTAMP NOT NULL,
                         status VARCHAR(50) NOT NULL
);

CREATE TABLE donation (
                          id VARCHAR(255) PRIMARY KEY,
                          donor_email VARCHAR(255) NOT NULL,
                          payment_id VARCHAR(255) NOT NULL,
                          donation_date TIMESTAMP NOT NULL,
                          FOREIGN KEY (donor_email) REFERENCES donor(email),
                          FOREIGN KEY (payment_id) REFERENCES payment(id)
);

CREATE TABLE help (
                      id VARCHAR(255) PRIMARY KEY,
                      beneficiary_email VARCHAR(255) NOT NULL,
                      payment_id VARCHAR(255) NOT NULL,
                      accident_description TEXT,
                      help_date TIMESTAMP NOT NULL,
                      FOREIGN KEY (beneficiary_email) REFERENCES beneficiary(email),
                      FOREIGN KEY (payment_id) REFERENCES payment(id)
);
