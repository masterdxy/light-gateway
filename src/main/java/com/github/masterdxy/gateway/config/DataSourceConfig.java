package com.github.masterdxy.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig extends HikariConfig {

    @NacosValue("${datasource.jdbc-url}")
    private String jdbcUrl;
    @NacosValue("${datasource.username}")
    private String username;
    @NacosValue("${datasource.password}")
    private String password;
    @NacosValue("${datasource.driver-class-name}")
    private String driverClassName;
    @NacosValue("${datasource.max-pool-size}")
    private int maxPoolSize;
    @NacosValue("${datasource.connection-timeout}")
    private long connectionTimeout;

    @Bean
    public DataSource dataSource() throws SQLException {
        return new HikariDataSource(fillParams());
    }

    private DataSourceConfig fillParams() {
        super.setJdbcUrl(jdbcUrl);
        super.setUsername(username);
        super.setPassword(password);
        super.setDriverClassName(driverClassName);
        super.setMaximumPoolSize(maxPoolSize);
        super.setConnectionTimeout(connectionTimeout);
        return this;
    }
}
