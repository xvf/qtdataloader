package com.qt.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Created by asrivastava on 5/8/17.
 */
@Configuration
public class DataSourceConfiguration {

    private final static Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);
    private final static String MYSQL_DRIVER="com.mysql.jdbc.Driver";
    @Autowired
    private Environment env;

    /**
     * Main NYC Master Data Source
     */
    @Bean(name = "mainDataSource")
    @Primary
//    @ConfigurationProperties("app.datasource.main")
    public javax.sql.DataSource mainDataSource() {
        final String user = this.env.getProperty("app.datasource.main.username");
        final String password = this.env.getProperty("app.datasource.main.password");
        final String url = this.env.getProperty("app.datasource.main.url");
        return this.getDataSource(url, user, password, MYSQL_DRIVER);
    }

    /**
     * Quartet Data Source
     * Gets MySQL Datasource
     * @return
     */
    @Bean(name = "qtDataSource")
//    @ConfigurationProperties("app.datasource.qt")
    public javax.sql.DataSource quartetDataSource() {
        final String user = this.env.getProperty("app.datasource.qt.username");
        final String password = this.env.getProperty("app.datasource.qt.password");
        final String url = this.env.getProperty("app.datasource.qt.url");
        return this.getDataSource(url, user, password, MYSQL_DRIVER);
    }

    @Bean
    BatchConfigurer configurer(DataSource dataSource) {
        return new DefaultBatchConfigurer(dataSource);
    }

    /**
     * Helper Method to generate a data source. If a different data source needs to be used for any of the
     * specific sources, a corresponding driver can be passed as a param
     * @param url
     * @param user
     * @param password
     * @return
     */
    private DriverManagerDataSource getDataSource(final String url, final String user, final String password, final String driver) {
        final DriverManagerDataSource mysql = new DriverManagerDataSource();
        mysql.setUrl(url);
        mysql.setUsername(user);
        mysql.setPassword(password);
        mysql.setDriverClassName(driver);
        return mysql;
    }


}
