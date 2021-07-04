package com.adamluptakosice.usercrdmin.infra;

import com.zaxxer.hikari.HikariConfig;

public class HikariConfigProvider {

    public static HikariConfig getConfig() {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:~/test");
        config.setUsername("sa");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("org.h2.Driver");
        return config;
    }
}
