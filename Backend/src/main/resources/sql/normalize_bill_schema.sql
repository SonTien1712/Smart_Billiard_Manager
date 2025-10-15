-- Normalized DDL for bill management domain
-- Scope: bills, billdetails, billiardtables
-- Notes:
--  - Keeps multi-tenant columns (CustomerID, ClubID) as in current design
--  - Adds sensible defaults, indexes, and CHECK constraints (MySQL 8+)
--  - Run in a maintenance window; back up data first

/* =========================
   BILLIARD TABLES
   ========================= */
DROP TABLE IF EXISTS `billiardtables`;
CREATE TABLE `billiardtables` (
  `TableID` INT NOT NULL AUTO_INCREMENT,
  `ClubID` INT NOT NULL,
  `CustomerID` INT NOT NULL,
  `TableName` VARCHAR(50) NOT NULL,
  -- Store type as short string; allowed values documented below
  `TableType` VARCHAR(20) NOT NULL,
  `HourlyRate` DECIMAL(10,2) NOT NULL,
  `TableStatus` VARCHAR(50) DEFAULT 'Available',
  `Location` VARCHAR(100) DEFAULT NULL,
  `PurchaseDate` DATE DEFAULT NULL,
  `LastMaintenanceDate` DATE DEFAULT NULL,
  `TableCondition` VARCHAR(50) DEFAULT 'Good',
  PRIMARY KEY (`TableID`),
  KEY `idx_club_table` (`ClubID`),
  KEY `idx_customer_table` (`CustomerID`),
  CONSTRAINT `fk_billiardtables_club`
    FOREIGN KEY (`ClubID`) REFERENCES `billardclub` (`ClubID`) ON DELETE CASCADE,
  CONSTRAINT `fk_billiardtables_customer`
    FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`) ON DELETE CASCADE,
  CONSTRAINT `chk_billiardtables_hourlyrate`
    CHECK (`HourlyRate` >= 0)
);
-- Allowed TableType values (application-level): 'Carom', 'Phăng', 'Lỗ'

/* =========================
   BILLS
   ========================= */
DROP TABLE IF EXISTS `bills`;
CREATE TABLE `bills` (
  `BillID` INT NOT NULL AUTO_INCREMENT,
  `ClubID` INT NOT NULL,
  `CustomerID` INT NOT NULL,
  `TableID` INT DEFAULT NULL,
  `EmployeeID` INT DEFAULT NULL,
  `StartTime` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `EndTime` DATETIME DEFAULT NULL,
  `TotalHours` DECIMAL(4,2) DEFAULT 0.00,
  `TotalTableCost` DECIMAL(10,2) DEFAULT 0.00,
  `TotalProductCost` DECIMAL(10,2) DEFAULT 0.00,
  `DiscountAmount` DECIMAL(10,2) DEFAULT 0.00,
  `PromotionID` INT DEFAULT NULL,
  `FinalAmount` DECIMAL(10,2) DEFAULT 0.00,
  `PaymentMethod` VARCHAR(50) DEFAULT NULL,
  `BillStatus` VARCHAR(50) DEFAULT 'Unpaid',
  `CreatedDate` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`BillID`),
  KEY `idx_bill_club` (`ClubID`),
  KEY `idx_bill_customer` (`CustomerID`),
  KEY `idx_bill_table` (`TableID`),
  KEY `idx_bill_employee` (`EmployeeID`),
  KEY `idx_bill_promotion` (`PromotionID`),
  KEY `idx_bill_status` (`BillStatus`),
  CONSTRAINT `fk_bills_club`
    FOREIGN KEY (`ClubID`) REFERENCES `billardclub` (`ClubID`) ON DELETE CASCADE,
  CONSTRAINT `fk_bills_customer`
    FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`) ON DELETE CASCADE,
  CONSTRAINT `fk_bills_table`
    FOREIGN KEY (`TableID`) REFERENCES `billiardtables` (`TableID`) ON DELETE SET NULL,
  CONSTRAINT `fk_bills_employee`
    FOREIGN KEY (`EmployeeID`) REFERENCES `employees` (`EmployeeID`) ON DELETE SET NULL,
  CONSTRAINT `fk_bills_promotion`
    FOREIGN KEY (`PromotionID`) REFERENCES `promotions` (`PromotionID`) ON DELETE SET NULL,
  CONSTRAINT `chk_bills_nonnegative`
    CHECK (`TotalHours` >= 0 AND `TotalTableCost` >= 0 AND `TotalProductCost` >= 0 AND `DiscountAmount` >= 0 AND `FinalAmount` >= 0)
);
-- Suggested PaymentMethod values: 'Cash','Card','Transfer','E-Wallet'
-- Suggested BillStatus values: 'Unpaid','Paid','Cancelled'

/* =========================
   BILL DETAILS
   ========================= */
DROP TABLE IF EXISTS `billdetails`;
CREATE TABLE `billdetails` (
  `BillDetailID` INT NOT NULL AUTO_INCREMENT,
  `BillID` INT NOT NULL,
  `ClubID` INT NOT NULL,
  `CustomerID` INT NOT NULL,
  `ProductID` INT DEFAULT NULL,
  `Quantity` INT DEFAULT 1,
  `UnitPrice` DECIMAL(10,2) NOT NULL,
  `SubTotal` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`BillDetailID`),
  KEY `idx_billdetail_bill` (`BillID`),
  KEY `idx_billdetail_product` (`ProductID`),
  KEY `idx_billdetail_club` (`ClubID`),
  KEY `idx_billdetail_customer` (`CustomerID`),
  CONSTRAINT `fk_billdetails_bill`
    FOREIGN KEY (`BillID`) REFERENCES `bills` (`BillID`) ON DELETE CASCADE,
  CONSTRAINT `fk_billdetails_product`
    FOREIGN KEY (`ProductID`) REFERENCES `products` (`ProductID`) ON DELETE SET NULL,
  CONSTRAINT `fk_billdetails_club`
    FOREIGN KEY (`ClubID`) REFERENCES `billardclub` (`ClubID`) ON DELETE CASCADE,
  CONSTRAINT `fk_billdetails_customer`
    FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`) ON DELETE CASCADE,
  CONSTRAINT `chk_billdetails_values`
    CHECK (`Quantity` > 0 AND `UnitPrice` >= 0 AND `SubTotal` >= 0)
);

-- End of normalization DDL

