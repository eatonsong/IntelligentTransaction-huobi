CREATE TABLE `trans_event` (
  `event_id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '使用策略',
  `state` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '状态',
  `total` varchar(255) DEFAULT NULL COMMENT '投入总金额',
  `earn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '预期赢利',
  `percent` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '预期百分比',
  PRIMARY KEY (`event_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
CREATE TABLE `trans_prepare_order` (
  `prepare_order_id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) DEFAULT NULL,
  `huo_order_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `symbol` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`prepare_order_id`) USING BTREE,
  KEY `fk_event_id` (`event_id`),
  CONSTRAINT `fk_event_id` FOREIGN KEY (`event_id`) REFERENCES `trans_event` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8;

CREATE TABLE `trans_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `prepare_order_id` int(11) DEFAULT NULL,
  `event_id` int(11) DEFAULT NULL,
  `huo_order_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币订单id',
  `huo_symbol` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币币种',
  `huo_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币交易类型',
  `huo_amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币数量',
  `huo_price` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币价格',
  `huo_state` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '火币订单状态',
  `huo_created_time` datetime DEFAULT NULL,
  `huo_canceled_time` datetime DEFAULT NULL,
  `huo_finished_time` datetime DEFAULT NULL,
  `huo_filled_cash_amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成交总额',
  `huo_filled_amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成交量',
  `huo_filled_fees` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手续费',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_order_event_id` (`event_id`),
  KEY `fk_prepare_order_id` (`prepare_order_id`),
  CONSTRAINT `fk_order_event_id` FOREIGN KEY (`event_id`) REFERENCES `trans_event` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE `trans_settle` (
  `settle_id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) DEFAULT NULL,
  `settle_state` varchar(255) DEFAULT NULL COMMENT '结算状态',
  `settle_total` varchar(255) DEFAULT NULL COMMENT '实际剩余u',
  `settle_earn` varchar(255) DEFAULT NULL COMMENT '实际赚取',
  `settle_percent` varchar(255) DEFAULT NULL COMMENT '实际百分比',
  PRIMARY KEY (`settle_id`),
  KEY `ft_settle_event_id` (`event_id`),
  CONSTRAINT `ft_settle_event_id` FOREIGN KEY (`event_id`) REFERENCES `trans_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trans_prepare_order` (
  `prepare_order_id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) DEFAULT NULL,
  `huo_order_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `symbol` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`prepare_order_id`) USING BTREE,
  KEY `fk_event_id` (`event_id`),
  CONSTRAINT `fk_event_id` FOREIGN KEY (`event_id`) REFERENCES `trans_event` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32313 DEFAULT CHARSET=utf8;

CREATE TABLE `trans_analysis_trade` (
  `trade_id` int(11) NOT NULL AUTO_INCREMENT,
  `strat_id` int(11) DEFAULT NULL,
  `result_id` int(11) DEFAULT NULL,
  `symbol` varchar(255) DEFAULT NULL,
  `init_money` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`trade_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

CREATE TABLE `trans_analysis_trade_result` (
  `result_id` int(11) NOT NULL AUTO_INCREMENT,
  `money` varchar(255) DEFAULT NULL,
  `count_buy_all` varchar(255) DEFAULT NULL,
  `count_sell_all` varchar(255) DEFAULT NULL,
  `high_money` varchar(255) DEFAULT NULL,
  `low_money` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`result_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;

CREATE TABLE `trans_analysis_trade_start` (
  `start_id` int(11) NOT NULL AUTO_INCREMENT,
  `up` varchar(255) DEFAULT NULL,
  `down` varchar(255) DEFAULT NULL,
  `up_order` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `down_order` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `upflag` varchar(255) DEFAULT NULL,
  `downflag` varchar(255) DEFAULT NULL,
  `buy_condition` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sell_condition` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`start_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;