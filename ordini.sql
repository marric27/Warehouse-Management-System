-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versione server:              12.0.2-MariaDB - mariadb.org binary distribution
-- S.O. server:                  Win64
-- HeidiSQL Versione:            12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dump della struttura del database wms
CREATE DATABASE IF NOT EXISTS `wms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;
USE `wms`;

-- Dump della struttura di tabella wms.checking_info
CREATE TABLE IF NOT EXISTS `checking_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_number` varchar(255) NOT NULL,
  `checkinginfo_code` varchar(13) NOT NULL,
  `expiration_date` date NOT NULL,
  `quantity` int(11) NOT NULL,
  `state` enum('CHECKED','CLOSED','OPEN','PUTAWAY') NOT NULL,
  `stock_unit_id` bigint(20) DEFAULT NULL,
  `grn_item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKluetww1f8je9lwn418870o2hy` (`checkinginfo_code`),
  UNIQUE KEY `UK2ojm4iqoekn63cm61l5otp11g` (`stock_unit_id`),
  KEY `FKcce73w8bxeumesk60ro4cwlpg` (`grn_item_id`),
  CONSTRAINT `FKcce73w8bxeumesk60ro4cwlpg` FOREIGN KEY (`grn_item_id`) REFERENCES `grn_item` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.checking_info: ~12 rows (circa)
INSERT INTO `checking_info` (`id`, `batch_number`, `checkinginfo_code`, `expiration_date`, `quantity`, `state`, `stock_unit_id`, `grn_item_id`) VALUES
	(3, 'BN-2025A', 'CI-01KCH3MJ01', '2025-12-31', 100, 'PUTAWAY', 3, 12),
	(4, 'BN-2025A', 'CI-01KCH3N98C', '2025-12-31', 100, 'PUTAWAY', 4, 13),
	(5, 'BN-2025A', 'CI-01KCH3NZB6', '2025-12-31', 100, 'PUTAWAY', 5, 14),
	(6, 'BN-2025A', 'CI-01KCH3PDRM', '2025-12-31', 100, 'PUTAWAY', 6, 15),
	(7, 'BN-2025A', 'CI-01KCH3Q6ZS', '2025-12-31', 100, 'PUTAWAY', 7, 16),
	(8, 'BN-2025A', 'CI-01KCH3SGKJ', '2025-12-31', 100, 'PUTAWAY', 8, 17),
	(9, 'BN-2025A', 'CI-01KCH3SSS7', '2025-12-31', 100, 'PUTAWAY', 9, 18),
	(10, 'BN-2025A', 'CI-01KCH3T3JE', '2025-12-31', 100, 'PUTAWAY', 10, 19),
	(11, 'BN-2025A', 'CI-01KCH3TD8R', '2025-12-31', 100, 'PUTAWAY', 11, 20),
	(12, 'BN-2025A', 'CI-01KCH3TP98', '2025-12-31', 100, 'PUTAWAY', 12, 21),
	(13, 'BN-2025A', 'CI-01KCH3V2CG', '2025-12-31', 100, 'PUTAWAY', 13, 22),
	(14, 'BN-2025A', 'CI-01KCH3V8JG', '2025-12-31', 100, 'PUTAWAY', 14, 23);

-- Dump della struttura di tabella wms.customers
CREATE TABLE IF NOT EXISTS `customers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billing_address` varchar(255) NOT NULL,
  `customer_code` varchar(255) NOT NULL,
  `email` varchar(150) NOT NULL,
  `name` varchar(100) NOT NULL,
  `shipping_address` varchar(255) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `tax_code` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiqv746oh5t5is1vr4p2nl79r6` (`customer_code`),
  UNIQUE KEY `UKrfbvkrffamfql7cjmen8v976v` (`email`),
  UNIQUE KEY `UK253rgoun08iktm7mphvodckby` (`tax_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.customers: ~3 rows (circa)
INSERT INTO `customers` (`id`, `billing_address`, `customer_code`, `email`, `name`, `shipping_address`, `surname`, `tax_code`) VALUES
	(1, 'Via Milano 20, Milan', 'CUST-01KCH38AJV', 'mario.rossie@example.com', 'Mario', 'string', 'Rossi', 'stringstringstri'),
	(2, 'Via Milano 20, Milan', 'CUST-01KCH38YXD', 'enzo@example.com', 'Enzo', 'string', 'Costa', '1234567891234567'),
	(3, 'Via Milano 20, Milan', 'CUST-01KCH3ZVVE', 'aldo@example.com', 'Aldo', 'string', 'Lino', '1234567891234576');

-- Dump della struttura di tabella wms.grn
CREATE TABLE IF NOT EXISTS `grn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grn_code` varchar(14) NOT NULL,
  `receiving_date` date NOT NULL,
  `state` enum('CHECKED','CLOSED','OPEN','PUTAWAY') NOT NULL,
  `supplier` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg6qvhk3s5ytqb7fi5jnfdvapl` (`grn_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.grn: ~3 rows (circa)
INSERT INTO `grn` (`id`, `grn_code`, `receiving_date`, `state`, `supplier`) VALUES
	(1, 'GRN-01KCH35D3X', '2025-11-21', 'CLOSED', 'ACME Corp'),
	(2, 'GRN-01KCH35H2E', '2025-11-21', 'CLOSED', 'qweCorp'),
	(3, 'GRN-01KCH35NT7', '2025-11-21', 'CLOSED', 'umb');

-- Dump della struttura di tabella wms.grn_item
CREATE TABLE IF NOT EXISTS `grn_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grn_item_code` varchar(15) NOT NULL,
  `compliant_qty` int(11) DEFAULT NULL,
  `expected_qty` int(11) NOT NULL,
  `not_compliant_qty` int(11) DEFAULT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `product_code` varchar(255) NOT NULL,
  `received_qty` int(11) DEFAULT NULL,
  `state` enum('CHECKED','CLOSED','OPEN','PUTAWAY') DEFAULT NULL,
  `grn_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK90oywgqk4v128o7ajny0kq6dp` (`grn_item_code`),
  KEY `FK75lv2xfix570xhfrt1pq3ymo8` (`grn_id`),
  CONSTRAINT `FK75lv2xfix570xhfrt1pq3ymo8` FOREIGN KEY (`grn_id`) REFERENCES `grn` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.grn_item: ~12 rows (circa)
INSERT INTO `grn_item` (`id`, `grn_item_code`, `compliant_qty`, `expected_qty`, `not_compliant_qty`, `notes`, `product_code`, `received_qty`, `state`, `grn_id`) VALUES
	(12, 'Item-01KCH3EJN4', 100, 100, 0, NULL, 'PRD-009', 100, 'PUTAWAY', 3),
	(13, 'Item-01KCH3EQ57', 100, 100, 0, NULL, 'PRD-008', 100, 'PUTAWAY', 3),
	(14, 'Item-01KCH3EVJ3', 100, 100, 0, NULL, 'PRD-007', 100, 'PUTAWAY', 3),
	(15, 'Item-01KCH3EY4Q', 100, 100, 0, NULL, 'PRD-001', 100, 'PUTAWAY', 3),
	(16, 'Item-01KCH3FASG', 100, 100, 0, NULL, 'PRD-001', 100, 'PUTAWAY', 2),
	(17, 'Item-01KCH3FDCC', 100, 100, 0, NULL, 'PRD-002', 100, 'PUTAWAY', 2),
	(18, 'Item-01KCH3FH81', 100, 100, 0, NULL, 'PRD-005', 100, 'PUTAWAY', 2),
	(19, 'Item-01KCH3FN99', 100, 100, 0, NULL, 'PRD-005', 100, 'PUTAWAY', 1),
	(20, 'Item-01KCH3FQTB', 100, 100, 0, NULL, 'PRD-001', 100, 'PUTAWAY', 1),
	(21, 'Item-01KCH3FT5G', 100, 100, 0, NULL, 'PRD-004', 100, 'PUTAWAY', 1),
	(22, 'Item-01KCH3GP89', 100, 100, 0, NULL, 'PRD-003', 100, 'PUTAWAY', 3),
	(23, 'Item-01KCH3GRP2', 100, 100, 0, NULL, 'PRD-006', 100, 'PUTAWAY', 3);

-- Dump della struttura di tabella wms.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `customer_code` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `state` enum('CLOSED','OPEN','PICKING') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgt3o4a5bqj59e9y6wakgk926t` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.orders: ~9 rows (circa)
INSERT INTO `orders` (`id`, `code`, `customer_code`, `date`, `state`) VALUES
	(1, 'ORD-01KCH40H1E', 'CUST-01KCH38AJV', '2025-01-15', 'OPEN'),
	(2, 'ORD-01KCH40SJ0', 'CUST-01KCH38AJV', '2025-01-15', 'OPEN'),
	(3, 'ORD-01KCH417FT', 'CUST-01KCH38YXD', '2025-01-15', 'OPEN'),
	(4, 'ORD-01KCH41J8K', 'CUST-01KCH38YXD', '2025-01-15', 'OPEN'),
	(5, 'ORD-01KCH41SDK', 'CUST-01KCH38YXD', '2025-01-15', 'OPEN'),
	(6, 'ORD-01KCH421PA', 'CUST-01KCH3ZVVE', '2025-01-15', 'OPEN'),
	(7, 'ORD-01KCH425SX', 'CUST-01KCH3ZVVE', '2025-01-15', 'OPEN'),
	(8, 'ORD-01KCH429K7', 'CUST-01KCH3ZVVE', '2025-01-15', 'OPEN'),
	(9, 'ORD-01KCH42A3S', 'CUST-01KCH3ZVVE', '2025-01-15', 'OPEN');

-- Dump della struttura di tabella wms.pick_list
CREATE TABLE IF NOT EXISTS `pick_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `customer_code` varchar(255) NOT NULL,
  `release_number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKa9kt0slrixmaaqf17ybmjjkvw` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.pick_list: ~0 rows (circa)

-- Dump della struttura di tabella wms.pick_list_item
CREATE TABLE IF NOT EXISTS `pick_list_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(255) NOT NULL,
  `state` enum('OPEN','PICKED') NOT NULL DEFAULT 'OPEN',
  `qty` int(11) NOT NULL,
  `picking_sequence` int(11) NOT NULL,
  `sales_order_code` varchar(255) NOT NULL,
  `sales_order_line_number` varchar(255) NOT NULL,
  `slot_code` varchar(255) NOT NULL,
  `pick_list_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKngxkuhfd23ypk4x4s1qx8w6r6` (`pick_list_id`),
  CONSTRAINT `FKngxkuhfd23ypk4x4s1qx8w6r6` FOREIGN KEY (`pick_list_id`) REFERENCES `pick_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.pick_list_item: ~0 rows (circa)

-- Dump della struttura di tabella wms.product
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` enum('CONTROLLED_DRUG','FLAMMABLE','REFRIGERATED','STANDARD') NOT NULL,
  `code` varchar(26) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKh3w5r1mx6d0e5c6um32dgyjej` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.product: ~0 rows (circa)

-- Dump della struttura di tabella wms.sales_order_line
CREATE TABLE IF NOT EXISTS `sales_order_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `sales_order_line_number` varchar(255) NOT NULL,
  `status` enum('CLOSED','OPEN','PICKING') NOT NULL,
  `order_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd5vd9jjc1m1bwlfg21gp6arqg` (`sales_order_line_number`),
  KEY `FKakbnv5shlqd7snqww6wsv01kw` (`order_id`),
  CONSTRAINT `FKakbnv5shlqd7snqww6wsv01kw` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.sales_order_line: ~27 rows (circa)
INSERT INTO `sales_order_line` (`id`, `product_code`, `quantity`, `sales_order_line_number`, `status`, `order_id`) VALUES
	(1, 'PRD-001', 10, 'SO-01KCH40H1H85H48CPDT8D69Z46', 'OPEN', 1),
	(2, 'PRD-002', 5, 'SO-01KCH40H1MW7Z8G0FTKE7EGGD8', 'OPEN', 1),
	(3, 'PRD-003', 20, 'SO-01KCH40H1NQKDTVJEY0N41C14F', 'OPEN', 1),
	(4, 'PRD-005', 10, 'SO-01KCH40SJVPTA89VAPQESKZCZY', 'OPEN', 2),
	(5, 'PRD-009', 5, 'SO-01KCH40SK4YNB8VVXS3G3618YP', 'OPEN', 2),
	(6, 'PRD-003', 20, 'SO-01KCH40SKCWHQ95HVXSW7JXP3N', 'OPEN', 2),
	(7, 'PRD-005', 10, 'SO-01KCH417FVPQEB4755MSRJQ8XR', 'OPEN', 3),
	(8, 'PRD-009', 5, 'SO-01KCH417FWCPJX32M219M06KDK', 'OPEN', 3),
	(9, 'PRD-004', 20, 'SO-01KCH417H1VVW95AZKDWTAA0BC', 'OPEN', 3),
	(10, 'PRD-007', 10, 'SO-01KCH41J8NFS69HWKH9WYXPVT1', 'OPEN', 4),
	(11, 'PRD-008', 5, 'SO-01KCH41J8PYHZK135C3CSX78K0', 'OPEN', 4),
	(12, 'PRD-001', 20, 'SO-01KCH41J9WVX2M8ZE2ZC1Z1PYT', 'OPEN', 4),
	(13, 'PRD-006', 10, 'SO-01KCH41SDNT80KWYR5AFAQMFA6', 'OPEN', 5),
	(14, 'PRD-004', 5, 'SO-01KCH41SDPXF1WBCF568DRT6BR', 'OPEN', 5),
	(15, 'PRD-001', 20, 'SO-01KCH41SDTZYXA9H4D7MK4W936', 'OPEN', 5),
	(16, 'PRD-007', 10, 'SO-01KCH421PB85DZTGZJHB0WG6MJ', 'OPEN', 6),
	(17, 'PRD-003', 5, 'SO-01KCH421PCK56JTRKVJAKTK4GD', 'OPEN', 6),
	(18, 'PRD-001', 20, 'SO-01KCH421PEZ736MPQDRNZ02DXF', 'OPEN', 6),
	(19, 'PRD-008', 10, 'SO-01KCH425T376A7TXMBMAN3DBC8', 'OPEN', 7),
	(20, 'PRD-002', 5, 'SO-01KCH425T4YVK1AY3C5C6TVKEE', 'OPEN', 7),
	(21, 'PRD-001', 20, 'SO-01KCH425T69H989J9RNQ2JWFQ5', 'OPEN', 7),
	(22, 'PRD-008', 10, 'SO-01KCH429K9BSPKX6YJR96N3A2C', 'OPEN', 8),
	(23, 'PRD-002', 5, 'SO-01KCH429KKFQSG2GSABY1PRCH4', 'OPEN', 8),
	(24, 'PRD-009', 20, 'SO-01KCH429KM8YMYQMZXBFYMANNP', 'OPEN', 8),
	(25, 'PRD-008', 10, 'SO-01KCH42A3VTBZT7XADR1SQ7Y8F', 'OPEN', 9),
	(26, 'PRD-002', 5, 'SO-01KCH42A3WJ4SZDNDZAVXG37KW', 'OPEN', 9),
	(27, 'PRD-009', 20, 'SO-01KCH42A3YX5ZJ84G7W1H8B1BN', 'OPEN', 9);

-- Dump della struttura di tabella wms.slot
CREATE TABLE IF NOT EXISTS `slot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowed_category` enum('CONTROLLED_DRUG','FLAMMABLE','REFRIGERATED','STANDARD') NOT NULL,
  `capacity` int(11) NOT NULL,
  `code` varchar(15) NOT NULL,
  `picking_sequence` int(11) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr8vjn5o43vd4b0t3dq85fx0hw` (`code`),
  KEY `FKdx1h3famy62v4x6i3xd6ibexu` (`product_id`),
  CONSTRAINT `FKdx1h3famy62v4x6i3xd6ibexu` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.slot: ~3 rows (circa)
INSERT INTO `slot` (`id`, `allowed_category`, `capacity`, `code`, `picking_sequence`, `product_id`) VALUES
	(1, 'CONTROLLED_DRUG', 100, 'SLOT-01KCH37YYG', '1', NULL),
	(2, 'CONTROLLED_DRUG', 100, 'SLOT-01KCH37ZDQ', '2', NULL),
	(3, 'CONTROLLED_DRUG', 100, 'SLOT-01KCH37ZZG', '3', NULL);

-- Dump della struttura di tabella wms.stock_units
CREATE TABLE IF NOT EXISTS `stock_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_number` varchar(255) NOT NULL,
  `product_category` enum('CONTROLLED_DRUG','FLAMMABLE','REFRIGERATED','STANDARD') NOT NULL,
  `code` varchar(14) NOT NULL,
  `expiration_date` date NOT NULL,
  `product_code` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `slot_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpe5nr9i8vyjlkqos85imyvdf5` (`code`),
  KEY `FKdfyxdtg992051lre68usus2i5` (`product_id`),
  KEY `FK1pt9nn6kxktp7de4nhxujnn48` (`slot_id`),
  CONSTRAINT `FK1pt9nn6kxktp7de4nhxujnn48` FOREIGN KEY (`slot_id`) REFERENCES `slot` (`id`),
  CONSTRAINT `FKdfyxdtg992051lre68usus2i5` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dump dei dati della tabella wms.stock_units: ~12 rows (circa)
INSERT INTO `stock_units` (`id`, `batch_number`, `product_category`, `code`, `expiration_date`, `product_code`, `quantity`, `product_id`, `slot_id`) VALUES
	(3, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3MHZZ', '2025-12-31', 'PRD-009', 100, NULL, 1),
	(4, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3N987', '2025-12-31', 'PRD-008', 100, NULL, 1),
	(5, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3NZB4', '2025-12-31', 'PRD-007', 100, NULL, 2),
	(6, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3PDRG', '2025-12-31', 'PRD-001', 100, NULL, 2),
	(7, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3Q6X8', '2025-12-31', 'PRD-001', 100, NULL, 2),
	(8, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3SGKD', '2025-12-31', 'PRD-002', 100, NULL, 3),
	(9, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3SSS4', '2025-12-31', 'PRD-005', 100, NULL, 3),
	(10, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3T3JC', '2025-12-31', 'PRD-005', 100, NULL, 3),
	(11, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3TD8N', '2025-12-31', 'PRD-001', 100, NULL, 1),
	(12, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3TP96', '2025-12-31', 'PRD-004', 100, NULL, 1),
	(13, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3V2BN', '2025-12-31', 'PRD-003', 100, NULL, 2),
	(14, 'BN-2025A', 'CONTROLLED_DRUG', 'STK-01KCH3V8JE', '2025-12-31', 'PRD-006', 100, NULL, 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
