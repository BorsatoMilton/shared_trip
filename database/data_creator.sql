-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: shared_trip
-- ------------------------------------------------------
-- Server version	8.0.38

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
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES ('2025-12-08 10:13:41',49,5,58,'ccc09278-393b-42e6-b5de-1eb4b88f0281');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `password_reset`
--

LOCK TABLES `password_reset` WRITE;
/*!40000 ALTER TABLE `password_reset` DISABLE KEYS */;
INSERT INTO `password_reset` VALUES (16,50,'2025-12-08 10:19:57','d3c4d17c-bf78-4154-9545-9d4f79111877',1);
/*!40000 ALTER TABLE `password_reset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `reservas`
--

LOCK TABLES `reservas` WRITE;
/*!40000 ALTER TABLE `reservas` DISABLE KEYS */;
INSERT INTO `reservas` VALUES ('2025-12-08',1,97,49,56,'CANCELADA',6677,NULL,1),('2025-12-08',1,96,49,57,'EN PROCESO',7333,NULL,1),('2025-12-08',1,98,50,58,'CONFIRMADA',5146,'ccc09278-393b-42e6-b5de-1eb4b88f0281',1),('2025-12-08',1,97,50,59,'EN PROCESO',4179,NULL,1);
/*!40000 ALTER TABLE `reservas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'admin'),(2,'usuario');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (41,'admin','$2a$12$7/RFhsGCIZJuh/hUHoGNRuDyiLgzQa653SPdidL20P0T3WdjLQ4O6','admin','admin','admin@gmail.com','3462629993',1,NULL),(48,'usuario1','$2a$12$LhkQ2GqOJp5sdZ8QAjJh/uzYDpAnkyAAzaE/B7ozQquzUOkoacvpG','usuario uno','usuario uno','usuario_uno@gmail.com','346262999',2,NULL),(49,'usuario2','$2a$12$Lz7zJfabiTMGgDfuCaWuO.Jn4D8a4bJ.4WNZEcH/tfGmywSo8wL4i','usuario dos','usuario dos','usuario_dos@gmail.com','034626211123',2,NULL),(50,'usuario3','$2a$12$GR9LaDtUOh7y/ek6RkS1rez0GlX3/U18SKr248kYdnZzcnRq1cWmW','usuario tres','usuario tres','usuario_tres@gmail.com','03462623422',2,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `vehiculos`
--

LOCK TABLES `vehiculos` WRITE;
/*!40000 ALTER TABLE `vehiculos` DISABLE KEYS */;
INSERT INTO `vehiculos` VALUES (40,'AA324TY','Toyota Corolla',2023,48,1),(41,'MNO678','Fiat Palio',2003,48,1),(42,'AC533TU','Honda Civic',2022,49,1);
/*!40000 ALTER TABLE `vehiculos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `viajes`
--

LOCK TABLES `viajes` WRITE;
/*!40000 ALTER TABLE `viajes` DISABLE KEYS */;
INSERT INTO `viajes` VALUES (96,'2025-12-11',3,'Rosario','La Plata',10000.00,0,'Moreno y Pellegrini',40,1),(97,'2025-12-13',3,'La Plata','Rosario',10000.00,0,'Plaza central',40,1),(98,'2025-12-07',1,'Rosario','Villa Cañás',15000.00,0,'Moreno 1829',42,1),(99,'2025-12-18',3,'Villa Gobernador Gálvez','Santa Fe',18000.00,0,'Centro',42,1);
/*!40000 ALTER TABLE `viajes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-08 10:33:01
