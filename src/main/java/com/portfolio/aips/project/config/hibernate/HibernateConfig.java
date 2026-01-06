package com.portfolio.aips.project.config.hibernate;

import com.portfolio.aips.project.interceptor.SQLStatementInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@Slf4j
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
        log.info("jpa properties: {}", jpaProperties.getProperties());
        properties.putAll(jpaProperties.getProperties());

        // 기존 설정
        properties.put("hibernate.session_factory.statement_inspector", inspector);
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");

        //  Hibernate batch insert 옵션 추가
        properties.put("hibernate.jdbc.batch_size", "50");        // 한 번에 묶을 row 개수
        properties.put("hibernate.order_inserts", "true");       // insert 순서 정렬
        properties.put("hibernate.order_updates", "true");       // update 순서 정렬 (필요시)
        properties.put("hibernate.generate_statistics", "true"); // 통계 확인용 (옵션)

        //  2차 캐시 활성화

        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        properties.put("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");

        properties.put("hibernate.cache.use_second_level_cache", true);
        properties.put("hibernate.cache.use_query_cache", true);

        emf.setJpaProperties(properties);

        return emf;
    }
}
