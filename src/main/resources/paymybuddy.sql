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
  email VARCHAR(100) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  remember_me BOOLEAN DEFAULT false,
  iban VARCHAR(34),
  bank VARCHAR(100),
  balance INTEGER DEFAULT 0
  );

-- Default values for table 'user'

INSERT INTO user (email,first_name,last_name,password,iban,bank,balance)
VALUES
('acall@mail.fr','Arthur','Call','Excalibur','FR00123456789','Camelot',123),
('mking@mail.fr','Midas','King','Gold','FR00234567891','Phrygie Bank',901),
('jpoor@mail.fr','Job','Poor','Nothing','FR00345678912','Sanzinron Bank',0),
('bpicsou@mail.fr','Balthazar','Picsou','Money','FR00456789123','Duck Bank',599);

--
-- Table structure for table `connection`
--

CREATE TABLE connection (
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (buddy_id) REFERENCES user(id),
  PRIMARY KEY (user_id,buddy_id)
  );

-- Default values for table 'connection'

INSERT INTO connection (user_id,buddy_id)
VALUES
(1,2),(1,3),(2,1),(2,4),(4,1),(4,2),(4,3);

--
-- Table structure for table `transaction`
--

CREATE TABLE transaction (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(255),
  amount INTEGER NOT NULL,
  date DATE NOT NULL,
  issuer_id INTEGER NOT NULL,
  recipient_id INTEGER NOT NULL,
  FOREIGN KEY (issuer_id) REFERENCES user(id),
  FOREIGN KEY (recipient_id) REFERENCES user(id)
  );

--
-- Table structure for table `bank_operation`
--

CREATE TABLE bank_operation (
  transaction_id INTEGER NOT NULL PRIMARY KEY,
  done BOOLEAN DEFAULT false,
  FOREIGN KEY (transaction_id) REFERENCES transaction(id)
  );

