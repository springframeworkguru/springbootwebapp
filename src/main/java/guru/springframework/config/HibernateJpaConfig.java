package guru.springframework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jt on 1/7/16.
 */
@Configuration
public class HibernateJpaConfig {

    private Map<String, String> properties = new HashMap<String, String>();

    public HibernateJpaConfig() {
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.DefaultComponentSafeNamingStrategy");
    }

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private PersistenceUnitManager persistenceUnitManager;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(false);
        adapter.setDatabase(Database.H2);
        adapter.setDatabasePlatform("H2");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter) {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
                jpaVendorAdapter, properties,
                this.persistenceUnitManager);
        builder.setCallback(null);
        return builder;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
        Map<String, Object> vendorProperties = new LinkedHashMap<String, Object>();
        vendorProperties.putAll(properties);

        return factoryBuilder.dataSource(this.dataSource).packages("guru.springframework.domain")
                .properties(vendorProperties).jta(false).build();
    }
}
