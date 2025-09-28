package com.portfolio.aips.project.config.hibernate;

import com.portfolio.aips.project.interceptor.SQLStatementInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

    private final SQLStatementInterceptor inspector;
    private final DataSource dataSource;
    private final JpaProperties jpaProperties;

    public HibernateConfig(SQLStatementInterceptor inspector, DataSource dataSource, JpaProperties jpaProperties) {
        this.inspector = inspector;
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.portfolio");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Map -> Properties 변환
        Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());

        properties.put("hibernate.session_factory.statement_inspector", inspector);
        emf.setJpaProperties(properties);

        return emf;
    }
}
