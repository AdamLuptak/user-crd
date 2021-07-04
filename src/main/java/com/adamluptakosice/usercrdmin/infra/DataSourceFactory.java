package com.adamluptakosice.usercrdmin.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    private HikariConfig config;

    public DataSourceFactory(HikariConfig config) {
        this.config = config;
    }

    public DataSource create() {
        return new HikariDataSource(config);
    }
}
