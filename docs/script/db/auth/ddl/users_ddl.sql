--  用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '用户密码密文',
  `name` varchar(200) DEFAULT NULL COMMENT '用户姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '用户手机',
  `enabled` bit(1) DEFAULT NULL COMMENT '是否有效用户',
  `account_non_expired` bit(1) DEFAULT NULL COMMENT '账号是否未过期',
  `credentials_non_expired` bit(1) DEFAULT NULL COMMENT '密码是否未过期',
  `account_non_locked` bit(1) DEFAULT NULL COMMENT '是否未锁定',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(100) NOT NULL COMMENT '创建人',
  `update_by` varchar(100) NOT NULL COMMENT '更新人',
  `disabled` int(11) DEFAULT '0' COMMENT '数据是否有效(0 有效 1 无效)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_users_username` (`username`),
  UNIQUE KEY `ux_users_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 COMMENT='用户表';

--  角色表
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `code` varchar(100) NOT NULL COMMENT '角色code',
  `name` varchar(200) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(500) DEFAULT NULL COMMENT '简介',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(100) NOT NULL COMMENT '创建人',
  `update_by` varchar(100) NOT NULL COMMENT '更新人',
  `disabled` int(11) DEFAULT '0' COMMENT '数据是否有效(0 有效 1 无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- 资源表
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源id',
  `code` varchar(100) DEFAULT NULL COMMENT '资源code',
  `type` varchar(100) DEFAULT NULL COMMENT '资源类型(10 左侧菜单 20 top菜单 30 接口URL 40 按钮)',
  `name` varchar(200) DEFAULT NULL COMMENT '资源名称',
  `url` varchar(200) DEFAULT NULL COMMENT '资源url',
  `method` varchar(20) DEFAULT NULL COMMENT '资源方法',
  `description` varchar(500) DEFAULT NULL COMMENT '简介',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(100) NOT NULL COMMENT '创建人',
  `update_by` varchar(100) NOT NULL COMMENT '更新人',
  `disabled` int(11) DEFAULT '0' COMMENT '数据是否有效(0 有效 1 无效)',
  `sort` int(10) DEFAULT '0' COMMENT '排序',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父ID',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_resources_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8 COMMENT='资源表';

-- 用户和角色关系表
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(100) NOT NULL COMMENT '创建人',
  `update_by` varchar(100) NOT NULL COMMENT '更新人',
  `disabled` int(11) DEFAULT '0' COMMENT '数据是否有效(0 有效 1 无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8 COMMENT='用户和角色关系表';

-- 角色和资源关系表
DROP TABLE IF EXISTS `t_role_resource`;
CREATE TABLE `t_role_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系id',
  `resource_id` int(11) NOT NULL COMMENT '角色id',
  `role_id` int(11) NOT NULL COMMENT '资源id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(100) NOT NULL COMMENT '创建人',
  `update_by` varchar(100) NOT NULL COMMENT '更新人',
  `disabled` int(11) DEFAULT '0' COMMENT '数据是否有效(0 有效 1 无效)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8 COMMENT='角色和资源关系表';