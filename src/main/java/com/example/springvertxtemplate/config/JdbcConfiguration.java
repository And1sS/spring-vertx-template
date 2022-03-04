package com.example.springvertxtemplate.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.SqlClient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Configuration
public class JdbcConfiguration {

    @Bean
    public JsonObject jdbcPoolProperties(JdbcProperties jdbcProperties) {
        return new JsonObject()
                .put("driver_class", "org.postgresql.Driver")
                .put("max_pool_size", jdbcProperties.connectionPoolSize)
                .put("url", "jdbc:postgresql://" + jdbcProperties.url)
                .put("user", jdbcProperties.user)
                .put("password", jdbcProperties.password);
    }

    @Bean
    public JDBCPool jdbcPool(Vertx vertx, JsonObject jdbcPoolProperties) {
        return JDBCPool.pool(vertx, jdbcPoolProperties);
    }

    @Bean
    public SqlClient sqlClient(JDBCPool jdbcPool) {
        return jdbcPool;
    }

    @Data
    @Validated
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "database")
    public static class JdbcProperties {

        @NotEmpty
        String url;

        @NotEmpty
        String user;

        @NotEmpty
        String password;

        @NotNull
        @Min(1)
        Integer connectionPoolSize;
    }
}
