-- Create syntax for TABLE 'gw_access_credentials'
CREATE TABLE `gw_access_credentials` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `namespace_id` varchar(32) NOT NULL DEFAULT '' COMMENT '命名空间ID',
  `access_key` varchar(128) NOT NULL DEFAULT '' COMMENT 'AccessKey',
  `access_secret` varchar(256) NOT NULL DEFAULT '' COMMENT 'AccessSecret',
  `expire_in_second` int(11) DEFAULT '31536000' COMMENT '凭证有效期,默认一年',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create syntax for TABLE 'gw_endpoint_config'
CREATE TABLE `gw_endpoint_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `namespace_id` varchar(32) NOT NULL DEFAULT '' COMMENT '命名空间ID',
  `upstream_type` varchar(32) NOT NULL DEFAULT 'dubbo' COMMENT '上游类型,默认dubbo',
  `uri_pattern` varchar(64) NOT NULL DEFAULT '' COMMENT '请求路径,ant style',
  `upstream_url` varchar(128) NOT NULL DEFAULT '' COMMENT '上游地址',
  `is_need_auth` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否需要授权',
  `is_need_sign` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否验证签名',
  `is_mock` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是mock响应',
  `timeout_in_ms` int(11) DEFAULT NULL COMMENT '上游处理超时时间',
  `version` varchar(32) NOT NULL DEFAULT '1.0.0' COMMENT '版本号',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- Create syntax for TABLE 'gw_endpoint_mock_data'
CREATE TABLE `gw_endpoint_mock_data` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `endpoint_id` int(11) DEFAULT NULL COMMENT '命名空间ID',
  `mock_data` varchar(255) DEFAULT NULL COMMENT 'mock数据,JSON',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create syntax for TABLE 'gw_endpoint_rate_limit'
CREATE TABLE `gw_endpoint_rate_limit` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `overdraft` int(11) DEFAULT NULL COMMENT '初始总量',
  `greed_token_size` int(11) DEFAULT NULL COMMENT '填充个数',
  `refill_second` int(11) DEFAULT NULL COMMENT '填充时间秒',
  `endpoint_id` int(11) NOT NULL COMMENT '端点ID',
  `limit_policy` varchar(11) DEFAULT NULL COMMENT '限流策略',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create syntax for TABLE 'gw_namespace_conf'
CREATE TABLE `gw_namespace_conf` (
  `namespace_id` varchar(32) NOT NULL DEFAULT '' COMMENT '命名空间ID',
  `namespace_name` varchar(64) DEFAULT NULL COMMENT '命名空间名称',
  PRIMARY KEY (`namespace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;