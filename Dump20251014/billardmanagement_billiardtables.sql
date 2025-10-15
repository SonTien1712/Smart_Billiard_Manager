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
-- Table structure for table `billiardtables`
--

DROP TABLE IF EXISTS `billiardtables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `billiardtables` (
  `TableID` int NOT NULL AUTO_INCREMENT,
  `ClubID` int NOT NULL,
  `CustomerID` int NOT NULL,
  `TableName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TableType` enum('Carom','Phăng','Lỗ') COLLATE utf8mb4_unicode_ci NOT NULL,
  `HourlyRate` decimal(10,2) NOT NULL,
  `TableStatus` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Available',
  `Location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `PurchaseDate` date DEFAULT NULL,
  `LastMaintenanceDate` date DEFAULT NULL,
  `TableCondition` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Good',
  PRIMARY KEY (`TableID`),
  KEY `CustomerID` (`CustomerID`),
  KEY `idx_club_table` (`ClubID`),
  CONSTRAINT `billiardtables_ibfk_1` FOREIGN KEY (`ClubID`) REFERENCES `billardclub` (`ClubID`) ON DELETE CASCADE,
  CONSTRAINT `billiardtables_ibfk_2` FOREIGN KEY (`CustomerID`) REFERENCES `customers` (`CustomerID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
