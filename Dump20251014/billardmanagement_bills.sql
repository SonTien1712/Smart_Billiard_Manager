-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: billardmanagement
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bills`
--

DROP TABLE IF EXISTS `bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bills` (
  `BillID` int NOT NULL AUTO_INCREMENT,
  `ClubID` int NOT NULL,
  `CustomerID` int NOT NULL,
  `TableID` int DEFAULT NULL,
  `EmployeeID` int DEFAULT NULL,
  `StartTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `EndTime` datetime DEFAULT NULL,
  `TotalHours` decimal(4,2) DEFAULT '0.00',
  `TotalTableCost` decimal(10,2) DEFAULT '0.00',
  `TotalProductCost` decimal(10,2) DEFAULT '0.00',
  `DiscountAmount` decimal(10,2) DEFAULT '0.00',
  `PromotionID` int DEFAULT NULL,
  `FinalAmount` decimal(10,2) DEFAULT '0.00',
  `PaymentMethod` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BillStatus` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Unpaid',
  `CreatedDate` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`BillID`),
  KEY `CustomerID` (`CustomerID`),
  KEY `EmployeeID` (`EmployeeID`),
  KEY `PromotionID` (`PromotionID`),
  KEY `idx_club_bill` (`ClubID`),
  KEY `idx_table_bill` (`TableID`),
  CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`ClubID`) REFERENCES `billardclub` (`ClubID`) ON DELETE CASCADE,
  CONSTRAINT `bills_ibfk_2` FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`) ON DELETE CASCADE,
  CONSTRAINT `bills_ibfk_3` FOREIGN KEY (`TableID`) REFERENCES `billiardtables` (`TableID`) ON DELETE SET NULL,
  CONSTRAINT `bills_ibfk_4` FOREIGN KEY (`EmployeeID`) REFERENCES `employees` (`EmployeeID`) ON DELETE SET NULL,
  CONSTRAINT `bills_ibfk_5` FOREIGN KEY (`PromotionID`) REFERENCES `promotions` (`PromotionID`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-14 21:44:35
