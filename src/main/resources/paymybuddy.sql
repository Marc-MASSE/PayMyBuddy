/* Setting up PayMyBuddy DB */

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(100) NOT NULL,
  `firstName` VARCHAR(100) NOT NULL,
  `lastName` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  'rememberMe' BOOLEAN DEFAULT false,
  'iban' VARCHAR(34),
  'bank' VARCHAR(100),
  'balance' INTEGER DEFAULT 0
  );

--
-- Table structure for table `connection`
--

DROP TABLE IF EXISTS `connection`;

CREATE TABLE `connection` (
  `user_id` INTEGER NOT NULL,
  `buddy_id` INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (buddy_id) REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (user_id,buddy_id)
  );

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `description` VARCHAR(255),
  `amount` INTEGER NOT NULL,
  'date' DATE NOT NULL,
  'issuer_id' INTEGER NOT NULL,
  'recipient_id' INTEGER NOT NULL,
  FOREIGN KEY (issuer_id) REFERENCES user(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (recipient_id) REFERENCES user(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
  );

--
-- Table structure for table `bank_operation`
--

DROP TABLE IF EXISTS `bank_operation`;

CREATE TABLE `bank_operation` (
  `transaction_id` INTEGER NOT NULL PRIMARY KEY,
  `done` BOOLEAN DEFAULT false,
  FOREIGN KEY (transaction_id) REFERENCES transaction(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
  );

