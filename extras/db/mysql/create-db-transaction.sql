--
-- CREATE ROLE transaction_db_user LOGIN PASSWORD 'GentleCorp21.08.2024';
--
-- CREATE DATABASE transaction_db;
--
-- GRANT ALL ON DATABASE transaction_db TO transaction_db_user;
--
-- CREATE TABLESPACE transaction_tablespace OWNER transaction_db_user LOCATION '/var/lib/postgresql/tablespace/transaction';
-- CREATE TABLESPACE transaction_tablespace OWNER transaction_db_user LOCATION '/Users/gentlebookpro/Projekte/GentleCorp-Ecosystem/volumes/tablespace/transaction';

-- (1) PowerShell:
--     cd extras\compose\mysql
--     docker compose up
-- (1) 2. PowerShell:
--     cd extras\compose\mysql
--     docker compose exec db bash
--         mysql --user=root --password=p < /sql/create-db-kunde.sql
--         exit
--     docker compose down

-- mysqlsh ist *NICHT* im Docker-Image enthalten: https://dev.mysql.com/doc/refman/8.2/en/mysql.html


CREATE USER IF NOT EXISTS 'transaction-db-user'@'%' IDENTIFIED BY 'GentleCorp11.03.2024';

-- 2. Erteilen Sie dem Benutzer grundlegende Zugriffsrechte.
GRANT USAGE ON *.* TO 'transaction-db-user'@'%';

-- 3. Erstellen Sie die Datenbank, falls sie nicht bereits existiert, und legen Sie die Zeichencodierung fest.
CREATE DATABASE IF NOT EXISTS `transaction-db` CHARACTER SET utf8;

-- 4. Erteilen Sie dem Benutzer alle Berechtigungen auf der neu erstellten Datenbank.
GRANT ALL ON `transaction-db`.* TO 'transaction-db-user'@'%';

-- 5. Erstellen Sie den Tablespace und fügen Sie eine Daten-Datei hinzu (optional, wenn Sie den Tablespace verwenden möchten).
CREATE TABLESPACE transaction_tablespace ADD DATAFILE 'transaction_tablespace.ibd' ENGINE=INNODB;
