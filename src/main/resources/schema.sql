/* Setting up PayMyBuddy DB */

--
-- Clear Database
--

DROP TABLE IF EXISTS connection;
DROP TABLE IF EXISTS bank_operation;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS user;

--
-- Table structure for table `user`
--

CREATE TABLE user (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  iban VARCHAR(34),
  bank VARCHAR(100),
  role VARCHAR(15) DEFAULT 'USER'
  );


--
-- Table structure for table `connection`
--

CREATE TABLE connection (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user(id)
  );


--
-- Table structure for table `transaction`
--

CREATE TABLE transaction (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  transaction_number INTEGER NOT NULL,
  description VARCHAR(255),
  amount VARCHAR(255),
  date DATE NOT NULL,
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  done BOOLEAN DEFAULT false,
  FOREIGN KEY (user_id) REFERENCES user(id)
  );

-- Default values for table 'transaction'

