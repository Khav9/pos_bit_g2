-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.7.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.15.0.7171
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for pos_bit
CREATE DATABASE IF NOT EXISTS `pos_bit` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;
USE `pos_bit`;

-- Dumping structure for table pos_bit.category
CREATE TABLE IF NOT EXISTS `category` (
  `category_description` varchar(255) DEFAULT NULL,
  `category_id` varchar(255) NOT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.category: ~2 rows (approximately)
INSERT INTO `category` (`category_description`, `category_id`, `category_name`, `status`) VALUES
	('category fruit', 'C001', 'Fruit', 'active'),
	('vegetable category', 'C002', 'Vegetable', 'active'),
	('beverages category', 'C003', 'Beverages', 'active');

-- Dumping structure for table pos_bit.cloud_vendor_info
CREATE TABLE IF NOT EXISTS `cloud_vendor_info` (
  `vendor_address` varchar(255) DEFAULT NULL,
  `vendor_id` varchar(255) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.cloud_vendor_info: ~0 rows (approximately)

-- Dumping structure for table pos_bit.order_items
CREATE TABLE IF NOT EXISTS `order_items` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL,
  `price_at_sale` double DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKlf6f9q956mt144wiv6p1yko16` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKlf6f9q956mt144wiv6p1yko16` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.order_items: ~5 rows (approximately)
INSERT INTO `order_items` (`item_id`, `order_id`, `price_at_sale`, `product_id`, `quantity`) VALUES
	(1, 1, 10, 1, 1),
	(2, 4, 10, 1, 1),
	(3, 5, 9, 3, 1),
	(4, 6, 61, 2, 2),
	(5, 7, 10, 1, 1);

-- Dumping structure for table pos_bit.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `total_amount` double DEFAULT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `cashier_username` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.orders: ~7 rows (approximately)
INSERT INTO `orders` (`order_id`, `total_amount`, `order_date`, `payment_status`, `cashier_username`, `note`, `payment_method`) VALUES
	(1, 10, '2026-02-21 16:10:37.101471', 'PAID', NULL, NULL, NULL),
	(2, 0, '2026-02-21 16:28:02.861802', 'PAID', NULL, NULL, NULL),
	(3, 0, '2026-02-21 16:28:39.653203', 'PAID', NULL, NULL, NULL),
	(4, 10, '2026-02-22 22:44:26.292702', 'PAID', 'admin_pos', NULL, 'TRANSFER'),
	(5, 9, '2026-02-22 22:46:44.383445', 'PAID', 'Est sit dolores lab', NULL, 'CASH'),
	(6, 122, '2026-02-22 23:05:58.264611', 'PAID', 'Est sit dolores lab', NULL, 'CASH'),
	(7, 10, '2026-02-22 23:18:26.652005', 'PAID', 'Reach chalaka', NULL, 'CASH');

-- Dumping structure for table pos_bit.product
CREATE TABLE IF NOT EXISTS `product` (
  `is_active` bit(1) DEFAULT NULL,
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_price` double NOT NULL CHECK (`product_price` >= 0),
  `product_quantity` int(11) DEFAULT NULL CHECK (`product_quantity` >= 0),
  `created_date` datetime(6) DEFAULT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `product_name` varchar(300) NOT NULL,
  `category_id` varchar(255) NOT NULL,
  `product_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),
  CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.product: ~3 rows (approximately)
INSERT INTO `product` (`is_active`, `product_id`, `product_price`, `product_quantity`, `created_date`, `modified_date`, `product_name`, `category_id`, `product_description`) VALUES
	(b'1', 1, 10, 20, NULL, '2026-02-22 23:18:26.757000', 'Apple1', 'C001', 'blue apple '),
	(b'1', 2, 61, 10, '2026-02-22 22:45:22.338000', '2026-02-22 23:05:58.306000', 'Quae ullam sequi qui', 'C002', 'Est magnam fugiat i'),
	(b'1', 3, 9, 66, '2026-02-22 22:45:31.518000', '2026-02-22 22:46:44.389000', 'Occaecat velit velit', 'C003', 'Dolorem mollit lauda');

-- Dumping structure for table pos_bit.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `roles` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table pos_bit.users: ~8 rows (approximately)
INSERT INTO `users` (`id`, `password`, `roles`, `username`, `address`, `email`, `full_name`, `phone`) VALUES
	(1, '$2a$10$Zf9dudVqAvuyOpjgU18CFO35o0UMNLkMktYyfwq1U1reRQ2cG3Gky', 'ROLE_USER', 'ppp1', NULL, NULL, NULL, NULL),
	(2, '$2a$10$k6ple47XUjYB.LMqtSrMB.OvcrIgso3tvebYFWMjkyum2Uji3N6V6', 'ROLE_USER', 'pos_admin', NULL, NULL, NULL, NULL),
	(3, '$2a$10$LJt4D8WCQD0D/qvHyQvZGeFskUHEdFAJhlyxDjbQq4Pf6EibcR1Ri', 'ROLE_USER,ROLE_ADMIN', 'admin_pos', NULL, NULL, NULL, NULL),
	(4, '$2a$10$2Es7cMOc6Y9HgdhlWo4R3OG3ZlIN7.3VsTaZYb.8Qt2gpPJiNPZe.', 'ROLE_USER', 'Est sit dolores lab', 'Sorla', 'johnsmith@gmail.com', 'Phanit Chea', '+855976315667'),
	(5, '$2a$10$D6fONNu3wcTBdXTxbLVjz.7PKse5tCyn/0lsQNGLTAIS8vjfZazuu', 'ROLE_USER', 'cashier01', 'Retail Branch', 'cashier01@posbit.local', 'Sok Dara', '+1-202-555-0101'),
	(6, '$2a$10$kqh7Cu08SjBAvampl.Jet./hGl8yDoZ5wWw800MmEBEzkTPKb76p2', 'ROLE_USER', 'cashier02', 'Retail Branch', 'cashier02@posbit.local', 'Lina Kim', '+1-202-555-0102'),
	(7, '$2a$10$r5H2t9UcxZPmxPjSGSoPmeW5oS/3WxYVEYXiwomEQ.it2ZtPCnga2', 'ROLE_USER', 'Baby Pnit', NULL, 'phanit.chea@gmail.com', 'Chea Phanit', NULL),
	(8, '$2a$10$FgwOvVo3Pr6qCVqhRiyOOOm6SDcHrLTXOPuK3UFQwE7KQUc.FuV.K', 'ROLE_USER', 'Reach chalaka', NULL, 'reach.one@gmail.com', 'Chhin Sereyputhirach', NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
