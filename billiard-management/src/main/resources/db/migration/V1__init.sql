-- ======================
-- Database & defaults
-- ======================
# CREATE DATABASE IF NOT EXISTS BillardManagement
#   CHARACTER SET utf8mb4
#   COLLATE utf8mb4_unicode_ci;
USE BillardManagement;

-- Đảm bảo InnoDB & timezone/strict mode do app cấu hình.
SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- ======================
-- Drop (nếu chạy thủ công, bỏ qua khi dùng Flyway)
-- ======================
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS Payments, BillDetails, Bills, Promotions, Inventory,
--   EmployeeShifts, EmployeeAccounts, Products, BilliardTables, Employees,
--   BillardClub, Customers;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ======================
-- 1) Customers (Owner)
-- ======================
CREATE TABLE IF NOT EXISTS Customers (
                                         CustomerID     INT AUTO_INCREMENT PRIMARY KEY,
                                         CustomerName   VARCHAR(255) NOT NULL,
    PhoneNumber    VARCHAR(20),
    Email          VARCHAR(255),
    Address        TEXT,
    DateJoined     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ExpiryDate     DATE,
    GoogleId       VARCHAR(255),
    IsActive       BOOLEAN NOT NULL DEFAULT TRUE,
    CreatedAt      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt      DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_customer_google (GoogleId),
    UNIQUE KEY uq_customer_email  (Email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 2) BillardClub (Branch)
-- ======================
CREATE TABLE IF NOT EXISTS BillardClub (
                                           ClubID       INT AUTO_INCREMENT PRIMARY KEY,
                                           CustomerID   INT NOT NULL,
                                           ClubName     VARCHAR(100) NOT NULL,
    Address      TEXT NOT NULL,
    CreatedAt    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt    DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_club_customer
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    KEY idx_club_customer (CustomerID),
    UNIQUE KEY uq_club_name_per_customer (CustomerID, ClubName)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 3) Employees
-- ======================
CREATE TABLE IF NOT EXISTS Employees (
                                         EmployeeID    INT AUTO_INCREMENT PRIMARY KEY,
                                         ClubID        INT NOT NULL,
                                         CustomerID    INT NOT NULL,
                                         EmployeeName  VARCHAR(255) NOT NULL,
    EmployeeType  ENUM('FullTime','PartTime') NOT NULL,
    PhoneNumber   VARCHAR(20),
    Email         VARCHAR(255),
    Address       TEXT,
    Salary        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    HourlyRate    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    DateHired     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    BankNumber    VARCHAR(50),
    BankName      VARCHAR(100),
    IsActive      BOOLEAN NOT NULL DEFAULT TRUE,
    CreatedAt     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt     DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_emp_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)     ON DELETE CASCADE,
    CONSTRAINT fk_emp_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)   ON DELETE CASCADE,
    KEY idx_emp_customer_club (CustomerID, ClubID),
    UNIQUE KEY uq_emp_email_per_customer (CustomerID, Email),
    UNIQUE KEY uq_emp_phone_per_customer (CustomerID, PhoneNumber)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 4) BilliardTables
-- ======================
CREATE TABLE IF NOT EXISTS BilliardTables (
                                              TableID            INT AUTO_INCREMENT PRIMARY KEY,
                                              ClubID             INT NOT NULL,
                                              CustomerID         INT NOT NULL,
                                              TableName          VARCHAR(50) NOT NULL,
    TableType          ENUM('POCKET','CAROM','SNOOKER') NOT NULL,  -- chuẩn hoá
    HourlyRate         DECIMAL(12,2) NOT NULL,
    TableStatus        ENUM('AVAILABLE','IN_USE','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    Location           VARCHAR(100),
    PurchaseDate       DATE,
    LastMaintenanceDate DATE,
    TableCondition     ENUM('GOOD','FAIR','POOR') NOT NULL DEFAULT 'GOOD',
    CreatedAt          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt          DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_table_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_table_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    KEY idx_table_status (CustomerID, ClubID, TableStatus),
    UNIQUE KEY uq_table_name_per_club (CustomerID, ClubID, TableName)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 5) Products
-- ======================
CREATE TABLE IF NOT EXISTS Products (
                                        ProductID      INT AUTO_INCREMENT PRIMARY KEY,
                                        ClubID         INT NOT NULL,
                                        CustomerID     INT NOT NULL,
                                        SKU            VARCHAR(64),                                -- optional, nhưng unique theo club nếu có
    ProductName    VARCHAR(255) NOT NULL,
    Price          DECIMAL(12,2) NOT NULL,
    CostPrice      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    Category       VARCHAR(100),
    ProductDescription TEXT,
    ProductUrl     VARCHAR(500),
    IsActive       BOOLEAN NOT NULL DEFAULT TRUE,
    CreatedDate    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt      DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_prod_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    KEY idx_prod_name (CustomerID, ClubID, ProductName),
    UNIQUE KEY uq_prod_sku_per_club (CustomerID, ClubID, SKU)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 6) Bills
-- ======================
CREATE TABLE IF NOT EXISTS Bills (
                                     BillID            INT AUTO_INCREMENT PRIMARY KEY,
                                     ClubID            INT NOT NULL,
                                     CustomerID        INT NOT NULL,
                                     TableID           INT,
                                     EmployeeID        INT,
                                     StartTime         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     EndTime           DATETIME NULL,
                                     TotalHours        DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    TotalTableCost    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    TotalProductCost  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    DiscountAmount    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    FinalAmount       DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PaymentMethod     VARCHAR(50),        -- MVP: 1 phương thức; nếu muốn đa phương thức => bảng Payments
    BillStatus        ENUM('UNPAID','PAID','VOID') NOT NULL DEFAULT 'UNPAID',
    CreatedDate       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt         DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_bill_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT fk_bill_table    FOREIGN KEY (TableID)    REFERENCES BilliardTables(TableID),
    CONSTRAINT fk_bill_emp      FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID),
    KEY idx_bill_tenant_date (CustomerID, ClubID, CreatedDate),
    CHECK (EndTime IS NULL OR EndTime >= StartTime)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- (Tuỳ chọn cho đa phương thức thanh toán trong tương lai)
-- CREATE TABLE Payments (
--   PaymentID   INT AUTO_INCREMENT PRIMARY KEY,
--   BillID      INT NOT NULL,
--   Method      VARCHAR(50) NOT NULL,
--   Amount      DECIMAL(12,2) NOT NULL,
--   PaidAt      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--   FOREIGN KEY (BillID) REFERENCES Bills(BillID) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 7) BillDetails
-- ======================
CREATE TABLE IF NOT EXISTS BillDetails (
                                           BillDetailID  INT AUTO_INCREMENT PRIMARY KEY,
                                           BillID        INT NOT NULL,
                                           ClubID        INT NOT NULL,
                                           CustomerID    INT NOT NULL,
                                           ProductID     INT,
                                           Quantity      INT NOT NULL DEFAULT 1,
                                           UnitPrice     DECIMAL(12,2) NOT NULL,
    SubTotal      DECIMAL(12,2) NOT NULL,
    AppliedPromotionID INT NULL,
    CONSTRAINT fk_bdet_bill    FOREIGN KEY (BillID)    REFERENCES Bills(BillID) ON DELETE CASCADE,
    CONSTRAINT fk_bdet_product FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
    CONSTRAINT fk_bdet_club    FOREIGN KEY (ClubID)    REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_bdet_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    KEY idx_bdet_bill (BillID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 8) EmployeeAccounts (login Staff/Admin)
-- ======================
CREATE TABLE IF NOT EXISTS EmployeeAccounts (
                                                UserID        INT AUTO_INCREMENT PRIMARY KEY,
                                                EmployeeID    INT UNIQUE,
                                                ClubID        INT NOT NULL,
                                                CustomerID    INT NOT NULL,
                                                Username      VARCHAR(50) NOT NULL,
    PasswordHash  VARCHAR(255) NOT NULL,
    Role          ENUM('STAFF','ADMIN') NOT NULL DEFAULT 'STAFF',
    LastLogin     DATETIME NULL,
    IsActive      BOOLEAN NOT NULL DEFAULT TRUE,
    CreatedDate   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt     DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ea_emp      FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)   ON DELETE CASCADE,
    CONSTRAINT fk_ea_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)    ON DELETE CASCADE,
    CONSTRAINT fk_ea_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)   ON DELETE CASCADE,
    UNIQUE KEY uq_username_global (Username),
    KEY idx_ea_tenant_emp (CustomerID, ClubID, EmployeeID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 9) EmployeeShifts (Chấm công / ca)
-- ======================
CREATE TABLE IF NOT EXISTS EmployeeShifts (
                                              ShiftID        INT AUTO_INCREMENT PRIMARY KEY,
                                              EmployeeID     INT NOT NULL,
                                              ClubID         INT NOT NULL,
                                              CustomerID     INT NOT NULL,
                                              ShiftDate      DATE NOT NULL,
                                              StartTime      TIME NULL,
                                              EndTime        TIME NULL,
                                              ActualStartTime DATETIME NULL,
                                              ActualEndTime   DATETIME NULL,
                                              HoursWorked    DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    OvertimeHours  DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    ShiftType      ENUM('MORNING','AFTERNOON','EVENING','NIGHT') NOT NULL,
    Status         ENUM('SCHEDULED','CHECKED_IN','COMPLETED','CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    CreatedAt      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt      DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_shift_emp      FOREIGN KEY (EmployeeID)  REFERENCES Employees(EmployeeID) ON DELETE CASCADE,
    CONSTRAINT fk_shift_club     FOREIGN KEY (ClubID)      REFERENCES BillardClub(ClubID)  ON DELETE CASCADE,
    CONSTRAINT fk_shift_customer FOREIGN KEY (CustomerID)  REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    CONSTRAINT chk_shift_time CHECK (ActualEndTime IS NULL OR ActualStartTime IS NULL OR ActualEndTime >= ActualStartTime),
    UNIQUE KEY uq_shift_unique (CustomerID, ClubID, EmployeeID, ShiftDate, ShiftType),
    KEY idx_shift_emp_date (EmployeeID, ShiftDate)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 10) Inventory (đơn giản)
-- ======================
CREATE TABLE IF NOT EXISTS Inventory (
                                         InventoryID   INT AUTO_INCREMENT PRIMARY KEY,
                                         ProductID     INT NOT NULL,
                                         ClubID        INT NOT NULL,
                                         CustomerID    INT NOT NULL,
                                         CurrentStock  INT NOT NULL DEFAULT 0,
                                         MinimumStock  INT NOT NULL DEFAULT 0,
                                         MaximumStock  INT NOT NULL DEFAULT 0,
                                         LastRestocked DATETIME NULL,
                                         Supplier      VARCHAR(255),
    SupplierContact VARCHAR(255),
    RestockLevel  INT NOT NULL DEFAULT 0,
    CreatedAt     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt     DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_product  FOREIGN KEY (ProductID)   REFERENCES Products(ProductID)   ON DELETE CASCADE,
    CONSTRAINT fk_inv_club     FOREIGN KEY (ClubID)      REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_inv_customer FOREIGN KEY (CustomerID)  REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    UNIQUE KEY uq_inventory_unique (CustomerID, ClubID, ProductID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
-- 11) Promotions
-- ======================
CREATE TABLE IF NOT EXISTS Promotions (
                                          PromotionID      INT AUTO_INCREMENT PRIMARY KEY,
                                          ClubID           INT NOT NULL,
                                          CustomerID       INT NOT NULL,
                                          PromotionName    VARCHAR(255) NOT NULL,
    PromotionCode    VARCHAR(50) UNIQUE,
    DiscountType     ENUM('PERCENTAGE','FIXED_AMOUNT') NOT NULL,
    DiscountValue    DECIMAL(12,2) NOT NULL,
    StartDate        DATETIME NULL,
    EndDate          DATETIME NULL,
    ApplicableTableTypes JSON NULL,  -- JSON thay vì TEXT (ví dụ: ["POCKET","CAROM"])
    MinPlayTime      DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    MinAmount        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    MaxDiscount      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    UsageLimit       INT NOT NULL DEFAULT 0,
    UsedCount        INT NOT NULL DEFAULT 0,
    IsActive         BOOLEAN NOT NULL DEFAULT TRUE,
    Description      TEXT,
    CreatedAt        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt        DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prom_club     FOREIGN KEY (ClubID)     REFERENCES BillardClub(ClubID)   ON DELETE CASCADE,
    CONSTRAINT fk_prom_customer FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
    KEY idx_prom_code_active (PromotionCode, IsActive),
    CHECK (EndDate IS NULL OR StartDate IS NULL OR EndDate >= StartDate)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
