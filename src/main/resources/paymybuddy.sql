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

-- Default values for table 'user'
-- password encrypted by https://bcrypt-generator.com/

INSERT INTO user (email,first_name,last_name,password,iban,bank,role)
VALUES
('acall@mail.fr','Arthur','Call','$2a$12$ad7CoCIaCW/VqS3ajYwbTuK3qcDPVfhK4wN7v49bvgfuBx8kpLYxu','FR00123456789','Camelot','USER'),
('mking@mail.fr','Midas','King','$2a$12$uUzR1iA5aeAGK/3VOnOumeGpHFNZ2m0oGh0uxv2/I0QBhvUAG0SoC','FR00234567891','Phrygie Bank','USER'),
('jpoor@mail.fr','Job','Poor','$2a$12$2z.2F1REBOgG5Ucw03cwjeSHSTTne7Ogh.gMaNr2g47fAam0.//ti','FR00345678912','Sanzinron Bank','USER'),
('bpicsou@mail.fr','Balthazar','Picsou','$2a$12$0mCUS7.q9DbA/Dj11WcIke77t.sM5hV6Zjv3U94LkwwA3/M6hW72C','FR00456789123','Duck Bank','USER'),
('admin@mail.fr','PayMyBuddy','','$2a$12$YhJHXY76WwFZ4SbTUzqxZOGM6aYyVkTJi2LFwWIpmtbm9cbZ.TgI2','FR00','Bank','ADMIN');

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
  amount VARCHAR(255),
  date DATE NOT NULL,
  user_id INTEGER NOT NULL,
  buddy_id INTEGER NOT NULL,
  done BOOLEAN DEFAULT false,
  FOREIGN KEY (user_id) REFERENCES user(id)
  );

-- Default values for table 'transaction'

INSERT INTO transaction (user_id,buddy_id,transaction_number,description,amount,date,done)
VALUES
(1,1,1,'Initial deposit','350.00','2022-01-01',false),
(2,2,2,'Initial deposit','1500.00','2022-01-01',false),
(4,4,3,'Initial deposit','900.00','2022-01-01',false),
(1,3,4,'Medical support','-100.00','2022-07-25',false),
(3,1,4,'Medical support','100.00','2022-07-25',false),
(2,3,5,'Medical support','-250.00','2022-08-21',false),
(3,2,5,'Medical support','250.00','2022-08-21',false),
(2,1,6,'Medical support','-200.00','2022-09-10',false),
(1,2,6,'Medical support','200.00','2022-09-10',false);

