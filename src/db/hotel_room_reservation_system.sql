-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: hotel_room_reservation_system
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `guests`
--

DROP TABLE IF EXISTS `guests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guests` (
  `guest_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `id_type` enum('Passport','Drivers License','National ID','Student ID') NOT NULL,
  `id_number` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guest_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guests`
--

LOCK TABLES `guests` WRITE;
/*!40000 ALTER TABLE `guests` DISABLE KEYS */;
INSERT INTO `guests` VALUES (1,'Demo','Guest','09123456789','Student ID','DEMO-0001','guest','guest123');
/*!40000 ALTER TABLE `guests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `packages`
--

DROP TABLE IF EXISTS `packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `packages` (
  `package_id` int NOT NULL AUTO_INCREMENT,
  `package_name` varchar(50) NOT NULL,
  `price` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`package_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `packages`
--

LOCK TABLES `packages` WRITE;
/*!40000 ALTER TABLE `packages` DISABLE KEYS */;
INSERT INTO `packages` VALUES (1,'Room Only',0,'Basic room stay without additional meals or services.'),(2,'Room + Breakfast',500,'Standard stay inclusive of daily breakfast service.'),(3,'Room + Amenities',1000,'Premium stay with full access to hotel leisure facilities.');
/*!40000 ALTER TABLE `packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `res_id` int DEFAULT NULL,
  `amount_paid` double DEFAULT NULL,
  `total_amount_due` double DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_type` varchar(50) DEFAULT NULL,
  `invoice_number` varchar(100) DEFAULT NULL,
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  KEY `res_id` (`res_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`res_id`) REFERENCES `reservations` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `res_id` int NOT NULL AUTO_INCREMENT,
  `guest_id` int NOT NULL,
  `room_id` int NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `package_id` int DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `amount_paid` decimal(10,2) DEFAULT NULL,
  `remaining_balance` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `checked_in_at` time DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `handled_by_id` int DEFAULT NULL,
  `checked_out_at` timestamp NULL DEFAULT NULL,
  `damage_fee` decimal(10,2) DEFAULT '0.00',
  `damage_paid` decimal(10,2) DEFAULT '0.00',
  `adults` int DEFAULT '0',
  `seniors` int DEFAULT '0',
  `pwds` int DEFAULT '0',
  `children` int DEFAULT '0',
  `cart_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`res_id`),
  KEY `guest_id` (`guest_id`),
  KEY `room_id` (`room_id`),
  KEY `fk_clerk` (`handled_by_id`),
  CONSTRAINT `fk_clerk` FOREIGN KEY (`handled_by_id`) REFERENCES `users` (`id`),
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `guests` (`guest_id`),
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_number` varchar(10) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` enum('Available','Booked','Maintenance','Occupied') DEFAULT 'Available',
  `package_type` varchar(50) DEFAULT 'Room Only',
  `amenities` text,
  `max_guest` int DEFAULT '2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_number` (`room_number`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (4,'101','Standard Room',1099.00,'Maintenance','Room Only','• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi',2),(5,'201','Deluxe Room',1599.00,'Maintenance','Room Only','• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi',2),(6,'301','Family Room',2099.00,'Available','Room Only','• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi',4),(7,'401','Specialty Room',2599.00,'Available','Room Only','• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi',2),(8,'102','Standard Room',1099.00,'Available','Room + Breakfast','• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi',2),(9,'202','Deluxe Room',1599.00,'Available','Room + Breakfast','• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi',2),(10,'302','Family Room',2099.00,'Available','Room + Breakfast','• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi',4),(11,'402','Specialty Room',2599.00,'Available','Room + Breakfast','• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi',2),(12,'103','Standard Room',1099.00,'Available','Room + Amenities','• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi',2),(13,'203','Deluxe Room',1599.00,'Available','Room + Amenities','• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi',2),(14,'303','Family Room',2099.00,'Maintenance','Room + Amenities','• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi',4),(15,'403','Specialty Room',2599.00,'Maintenance','Room + Amenities','• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi',2),(17,'105','Standard Room',1099.00,'Available','Room Only','• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi',2);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'System','Administrator','admin','admin123','Admin'),(2,'Demo','Receptionist','receptionist','receptionist123','Clerk');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-13 22:44:02
