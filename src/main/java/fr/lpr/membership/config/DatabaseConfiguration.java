package fr.lpr.membership.config;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableJpaRepositories("fr.lpr.membership.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
@Slf4j
public class DatabaseConfiguration implements EnvironmentAware {

    private Environment env;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean
    @ConditionalOnMissingClass("fr.lpr.membership.config.HerokuDatabaseConfiguration")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        if (env.getProperty("spring.datasource.url") == null && env.getProperty("spring.datasource.databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(env.getProperty("spring.datasource.dataSourceClassName"));
        if(StringUtils.isEmpty(env.getProperty("spring.datasource.url"))) {
            config.addDataSourceProperty("databaseName", env.getProperty("spring.datasource.databaseName"));
            config.addDataSourceProperty("serverName", env.getProperty("spring.datasource.serverName"));
        } else {
            config.addDataSourceProperty("url", env.getProperty("spring.datasource.url"));
        }
        config.addDataSourceProperty("user", env.getProperty("spring.datasource.username"));
        config.addDataSourceProperty("password", env.getProperty("spring.datasource.password"));

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }
        return new HikariDataSource(config);
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(env.getProperty("liquiBase.context"));
        log.debug("Configuring Liquibase");
        return liquibase;
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
}
