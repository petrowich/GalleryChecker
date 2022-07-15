package org.petrowich.gallerychecker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class ProdConfig {

    @Value("${POSTGRES_PORT}")
    private String dbPort;

    @Value("${POSTGRES_ADDRESS}")
    private String dbAddress;

    @Value("${POSTGRES_NAME}")
    private String dbName;

    @Value("${POSTGRES_USERNAME}")
    private String dbUserName;

    @Value("${POSTGRES_PASSWORD}")
    private String dbPassword;

    @Profile("prod")
    @Bean
    public DataSource getDataSource() {

        if (dbPort.isEmpty()){
            dbPort="5432";
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", dbAddress, dbPort, dbName);

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(url)
                .username(dbUserName)
                .password(dbPassword)
                .build();
    }
}
