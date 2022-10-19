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
  remember_me BOOLEAN DEFAULT false,
  iban VARCHAR(34),
  bank VARCHAR(100)
  );

-- Default values for table 'user'

INSERT INTO user (email,first_name,last_name,password,iban,bank)
VALUES
('acall@mail.fr','Arthur','Call','Excalibur','FR00123456789','Camelot'),
('mking@mail.fr','Midas','King','Gold','FR00234567891','Phrygie Bank'),
('jpoor@mail.fr','Job','Poor','Nothing','FR00345678912','Sanzinron Bank'),
('bpicsou@mail.fr','Balthazar','Picsou','Money','FR00456789123','Duck Bank');

--
-- Table structure for table `connection`
--

CREATE TABLE connection (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user(id)
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
  transaction_number INTEGER NOT NULL,
  description VARCHAR(255),
  amount INTEGER NOT NULL,
  date DATE NOT NULL,
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  done BOOLEAN DEFAULT false,
  FOREIGN KEY (user_id) REFERENCES user(id)
  );

-- Default values for table 'transaction'

INSERT INTO transaction (user_id,buddy_id,transaction_number,description,amount,date,done)
VALUES
(1,1,1,'Initial deposit',350,'2022-01-01',false),
(2,2,2,'Initial deposit',1500,'2022-01-01',false),
(4,4,3,'Initial deposit',900,'2022-01-01',false),
(1,3,4,'Medical support',-100,'2022-07-25',false),
(3,1,4,'Medical support',100,'2022-07-25',false),
(2,3,5,'Medical support',-250,'2022-08-21',false),
(3,2,5,'Medical support',250,'2022-08-21',false),
(2,1,6,'Medical support',-200,'2022-09-10',false),
(1,2,6,'Medical support',200,'2022-09-10',false);

