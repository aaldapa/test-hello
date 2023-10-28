package eus.ehu.audita.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "primaryEntityManagerFactory", 
		transactionManagerRef = "primaryTransactionManager", 
		basePackages = {"eus.ehu.audita.config", 
						"eus.ehu.audita.persistence"})
public class DatasourceConfig {

	@Primary
	@Bean(name = "primaryDataSourceProperties")
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "primaryDataSource")
	@ConfigurationProperties("spring.datasource.configuration")
	public DataSource dataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties primaryDataSourceProperties) {
		return primaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Primary
	@Bean(name = "primaryEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("primaryDataSource") DataSource primaryDataSource) {
		return builder
				.dataSource(primaryDataSource)
				.packages("eus.ehu.audita.model")
				.persistenceUnit("primary")
				.build();
	}

	@Primary
	@Bean(name = "primaryTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
		return new JpaTransactionManager(primaryEntityManagerFactory);
	}

}