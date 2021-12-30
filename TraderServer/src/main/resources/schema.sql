DROP TABLE contract_tradees;
DROP TABLE contract;
DROP TABLE company;

CREATE TABLE IF NOT EXISTS company(
  company_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  company_name VARCHAR(100) UNIQUE NOT NULL);
  
 CREATE INDEX IF NOT EXISTS company_name_idx_1 ON company(company_name);
  
CREATE TABLE IF NOT EXISTS contract(
  contract_id INTEGER PRIMARY KEY AUTO_INCREMENT,
  trader_id INTEGER UNIQUE NOT NULL);
  
CREATE TABLE IF NOT EXISTS contract_tradees(
   contract_tradees_id INTEGER PRIMARY KEY AUTO_INCREMENT,
   contract_id INTEGER NOT NULL,
   company_id INTEGER NOT NULL
);

ALTER TABLE contract_tradees ADD FOREIGN KEY (contract_id) REFERENCES contract(contract_id);
ALTER TABLE contract_tradees ADD FOREIGN KEY (company_id) REFERENCES company(company_id);