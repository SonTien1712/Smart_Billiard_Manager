-- ======================
-- Tạo Database
-- ======================
DROP DATABASE IF EXISTS BillardManagement;
CREATE DATABASE BillardManagement
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE BillardManagement;

-- ===============================================
-- 1. Bảng Customers (Chủ quán/Quản trị hệ thống)
-- ===============================================
CREATE TABLE Customers (
                           CustomerID INT AUTO_INCREMENT PRIMARY KEY,
                           CustomerName VARCHAR(255) NOT NULL,
                           PhoneNumber VARCHAR(20),
                           Email VARCHAR(255) UNIQUE,
                           Password VARCHAR(255),
                           Address TEXT,
                           DateJoined DATETIME DEFAULT CURRENT_TIMESTAMP,
                           ExpiryDate DATE, -- ngày hết hạn gói phần mềm
                           GoogleId VARCHAR(255),
                           IsActive BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- ======================================
-- 2. Bảng BillardClub (Chi nhánh CLB)
-- ======================================
CREATE TABLE BillardClub (
                             ClubID INT AUTO_INCREMENT PRIMARY KEY,
                             CustomerID INT NOT NULL,
                             ClubName VARCHAR(255) NOT NULL, -- Thêm tên chi nhánh
                             Address TEXT NOT NULL,
                             PhoneNumber VARCHAR(20), -- Thêm số điện thoại chi nhánh
                             FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
                                 ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================================
-- 3. Bảng Employees (Nhân viên)
-- ======================================
CREATE TABLE Employees (
                           EmployeeID INT AUTO_INCREMENT PRIMARY KEY,
                           ClubID INT NOT NULL,
                           CustomerID INT NOT NULL,
                           EmployeeName VARCHAR(255) NOT NULL,
                           EmployeeType ENUM('FullTime','PartTime') NOT NULL,
                           PhoneNumber VARCHAR(20),
                           Email VARCHAR(255),
                           Address TEXT,
                           Salary DECIMAL(10,2) DEFAULT 0.0,
                           HourlyRate DECIMAL(10,2) DEFAULT 0.0,
                           DateHired DATETIME DEFAULT CURRENT_TIMESTAMP,
                           BankNumber VARCHAR(50),
                           BankName VARCHAR(100),
                           IsActive BOOLEAN DEFAULT TRUE,
                           FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                           FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                           INDEX idx_club_employee (ClubID) -- Thêm Index
) ENGINE=InnoDB;

-- ===========================================
-- 4. Bảng BilliardTables (Bàn Bida)
-- ===========================================
CREATE TABLE BilliardTables (
                                TableID INT AUTO_INCREMENT PRIMARY KEY,
                                ClubID INT NOT NULL,
                                CustomerID INT NOT NULL,
                                TableName VARCHAR(50) NOT NULL, -- Đảm bảo tên bàn là cần thiết
                                TableType ENUM('Carom','Phăng','Lỗ') NOT NULL, -- Đảm bảo loại bàn
                                HourlyRate DECIMAL(10,2) NOT NULL,
                                TableStatus VARCHAR(50) DEFAULT 'Available',
                                Location VARCHAR(100),
                                PurchaseDate DATE,
                                LastMaintenanceDate DATE,
                                TableCondition VARCHAR(50) DEFAULT 'Good',
                                FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                                FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                                INDEX idx_club_table (ClubID) -- Thêm Index
) ENGINE=InnoDB;

-- =============================================
-- 5. Bảng Products (Đồ ăn, thức uống, Dịch vụ)
-- =============================================
CREATE TABLE Products (
                          ProductID INT AUTO_INCREMENT PRIMARY KEY,
                          ClubID INT NOT NULL,
                          CustomerID INT NOT NULL,
                          ProductName VARCHAR(255) NOT NULL,
                          Price DECIMAL(10,2) NOT NULL,
                          CostPrice DECIMAL(10,2) DEFAULT 0.0,
                          Category VARCHAR(100),
                          ProductDescription TEXT,
                          ProductUrl VARCHAR(500),
                          IsActive BOOLEAN DEFAULT TRUE,
                          CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                          FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                          INDEX idx_club_product (ClubID) -- Thêm Index
) ENGINE=InnoDB;

-- ============================================
-- 6. Bảng Promotions (Khuyến mãi) - TẠO TRƯỚC BILLS
-- ============================================
CREATE TABLE Promotions (
                            PromotionID INT AUTO_INCREMENT PRIMARY KEY,
                            ClubID INT NOT NULL,
                            CustomerID INT NOT NULL,
                            PromotionName VARCHAR(255) NOT NULL,
                            PromotionCode VARCHAR(50) UNIQUE,
                            DiscountType ENUM('Percentage','FixedAmount') NOT NULL,
                            DiscountValue DECIMAL(10,2) NOT NULL,
                            StartDate DATETIME,
                            EndDate DATETIME,
                            ApplicableTableTypes TEXT,
                            MinPlayTime DECIMAL(4,2) DEFAULT 0.0,
                            MinAmount DECIMAL(10,2) DEFAULT 0.0,
                            MaxDiscount DECIMAL(10,2) DEFAULT 0.0,
                            UsageLimit INT DEFAULT 0,
                            UsedCount INT DEFAULT 0,
                            IsActive BOOLEAN DEFAULT TRUE,
                            Description TEXT,
                            FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                            FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                            INDEX idx_club_promo (ClubID) -- Thêm Index
) ENGINE=InnoDB;

-- ======================================
-- 7. Bảng Bills (Hóa đơn)
-- ======================================
CREATE TABLE Bills (
                       BillID INT AUTO_INCREMENT PRIMARY KEY,
                       ClubID INT NOT NULL,
                       CustomerID INT NOT NULL,
                       TableID INT,
                       EmployeeID INT,
                       StartTime DATETIME DEFAULT CURRENT_TIMESTAMP,
                       EndTime DATETIME,
                       TotalHours DECIMAL(4,2) DEFAULT 0.0,
                       TotalTableCost DECIMAL(10,2) DEFAULT 0.0,
                       TotalProductCost DECIMAL(10,2) DEFAULT 0.0,
                       DiscountAmount DECIMAL(10,2) DEFAULT 0.0,
                       PromotionID INT NULL,
                       FinalAmount DECIMAL(10,2) DEFAULT 0.0,
                       PaymentMethod VARCHAR(50),
                       BillStatus VARCHAR(50) DEFAULT 'Unpaid',
                       CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                       FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                       FOREIGN KEY (TableID) REFERENCES BilliardTables(TableID) ON DELETE SET NULL, -- SET NULL nếu bàn bị xoá
                       FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) ON DELETE SET NULL, -- SET NULL nếu nhân viên bị xoá
                       FOREIGN KEY (PromotionID) REFERENCES Promotions(PromotionID) ON DELETE SET NULL,
                       INDEX idx_club_bill (ClubID), -- Thêm Index
                       INDEX idx_table_bill (TableID) -- Thêm Index
) ENGINE=InnoDB;

-- ======================================
-- 8. Bảng BillDetails (Chi tiết hóa đơn)
-- ======================================
CREATE TABLE BillDetails (
                             BillDetailID INT AUTO_INCREMENT PRIMARY KEY,
                             BillID INT NOT NULL,
                             ClubID INT NOT NULL,
                             CustomerID INT NOT NULL,
                             ProductID INT,
                             Quantity INT DEFAULT 1,
                             UnitPrice DECIMAL(10,2) NOT NULL,
                             SubTotal DECIMAL(10,2) NOT NULL,
                             FOREIGN KEY (BillID) REFERENCES Bills(BillID) ON DELETE CASCADE,
                             FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE SET NULL, -- SET NULL nếu sản phẩm bị xoá
                             FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                             FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                             INDEX idx_bill_detail (BillID) -- Thêm Index
) ENGINE=InnoDB;

-- =========================================
-- 9. Bảng EmployeeAccounts (Tài khoản nhân viên)
-- =========================================
CREATE TABLE EmployeeAccounts (
                                  UserID INT AUTO_INCREMENT PRIMARY KEY,
                                  EmployeeID INT UNIQUE,
                                  ClubID INT NOT NULL,
                                  CustomerID INT NOT NULL,
                                  Username VARCHAR(50) UNIQUE NOT NULL,
                                  PasswordHash VARCHAR(255) NOT NULL,
                                  LastLogin DATETIME,
                                  IsActive BOOLEAN DEFAULT TRUE,
                                  CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) ON DELETE CASCADE,
                                  FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                                  FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================================
-- 10. Bảng EmployeeShifts (Chấm công / ca làm)
-- ======================================
CREATE TABLE EmployeeShifts (
                                ShiftID INT AUTO_INCREMENT PRIMARY KEY,
                                EmployeeID INT NOT NULL,
                                ClubID INT NOT NULL,
                                CustomerID INT NOT NULL,
                                ShiftDate DATE NOT NULL,
                                StartTime TIME,
                                EndTime TIME,
                                ActualStartTime DATETIME,
                                ActualEndTime DATETIME,
                                HoursWorked DECIMAL(4,2) DEFAULT 0.0,
                                OvertimeHours DECIMAL(4,2) DEFAULT 0.0,
                                ShiftType ENUM('Sáng','Chiều','Tối','Đêm'),
                                Status VARCHAR(50) DEFAULT 'Scheduled',
                                FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) ON DELETE CASCADE,
                                FOREIGN KEY (ClubID) REFERENCES BillardClub(ClubID) ON DELETE CASCADE,
                                FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE,
                                INDEX idx_employee_shift (EmployeeID) -- Thêm Index
) ENGINE=InnoDB;

-- ======================================
-- 11. Bảng Admins (Tài khoản quản trị)
-- ======================================
CREATE TABLE Admins (
                        AdminID INT AUTO_INCREMENT PRIMARY KEY,
                        Username VARCHAR(50) UNIQUE NOT NULL,
                        PasswordHash VARCHAR(255) NOT NULL,
                        Email VARCHAR(255) UNIQUE,
                        CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                        LastLogin DATETIME,
                        IsActive BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- ======================================
-- 12. Bảng PasswordResetTokens (Quản lý reset password)
-- ======================================
CREATE TABLE PasswordResetTokens (
                                     TokenID INT AUTO_INCREMENT PRIMARY KEY,
                                     UserType ENUM('Admin','Customer','Employee') NOT NULL,
                                     UserID INT NOT NULL,
                                     Token VARCHAR(255) NOT NULL,
                                     ExpiryDate DATETIME NOT NULL,
                                     CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     CONSTRAINT uq_token UNIQUE (Token),
                                     INDEX idx_user_type (UserType, UserID) -- Giúp truy vấn nhanh token của user
) ENGINE=InnoDB;

