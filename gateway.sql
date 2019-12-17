CREATE TABLE `gw_endpoint_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uriPattern` varchar(64) NOT NULL DEFAULT '',
  `upstream_type` varchar(32) NOT NULL DEFAULT 'dubbo',
  `upstream_url` varchar(128) NOT NULL DEFAULT '',
  `is_need_auth` tinyint(1) NOT NULL DEFAULT '1',
  `is_need_sign` tinyint(1) NOT NULL DEFAULT '0',
  `is_mock` tinyint(1) NOT NULL DEFAULT '0',
  `namespace` varchar(32) NOT NULL DEFAULT '',
  `version` varchar(32) NOT NULL DEFAULT '1.0.0',
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gw_endpoint_mock_data` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `endpoint_id` int(11) DEFAULT NULL,
  `mock_data` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gw_endpoint_rate_limit` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `limit_policy` varchar(11) DEFAULT NULL,
  `overdraft` int(11) DEFAULT NULL,
  `greed_token_size` int(11) DEFAULT NULL,
  `refill_second` int(11) DEFAULT NULL,
  `endpoint_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;