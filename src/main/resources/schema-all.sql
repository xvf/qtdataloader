DROP TABLE IF EXISTS `NYC_MASTER_CLINIC_DATA`;
CREATE TABLE `NYC_MASTER_CLINIC_DATA` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name_1` varchar(256) DEFAULT NULL,
  `name_2` varchar(200) DEFAULT 'no-name',
  `city` varchar(50) DEFAULT NULL,
  `latitude` varchar(100) DEFAULT NULL,
  `longitude` varchar(100) DEFAULT NULL,
  `zip` varchar(50) DEFAULT NULL,
  `street_address` varchar(199) DEFAULT NULL,
  `phone` varchar(123) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `hash_id` varchar(100) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  `matched_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UniqueClinic` (`name_1`,`name_2`,`street_address`,`zip`),
  UNIQUE KEY `hash_unq` (`hash_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;