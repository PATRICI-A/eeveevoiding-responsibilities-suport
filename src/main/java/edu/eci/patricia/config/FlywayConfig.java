package edu.eci.patricia.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.EntityManagerFactory;

/**
 * Manually configures Flyway database migrations.
 * <p>
 * Spring Boot 4.x removed the built-in {@code FlywayAutoConfiguration} from
 * {@code spring-boot-autoconfigure}, so Flyway is no longer auto-configured
 * and must be set up explicitly.
 */
@Configuration
@Profile({"qa", "prod"})
public class FlywayConfig {

    private static final String MIGRATIONS_LOCATION = "classpath:db/migration";

    /**
     * Creates and runs Flyway migrations on startup.
     * Calls {@code repair()} first to fix any checksum mismatches from
     * previously-applied migrations, then {@code migrate()} to apply any
     * pending migrations. Both run <strong>before</strong> Hibernate
     * validates the schema.
     *
     * @param dataSource the configured {@link DataSource}
     * @return a fully initialised {@link Flyway} instance
     */
    @Bean
    public Flyway flyway(final DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(MIGRATIONS_LOCATION)
                .baselineOnMigrate(true)
                .load();
        flyway.repair();
        flyway.migrate();
        return flyway;
    }

    /**
     * Ensures the {@link EntityManagerFactory} bean depends on the {@code flyway}
     * bean so that migrations run <strong>before</strong> Hibernate validates
     * the database schema.
     */
    @Bean
    static BeanFactoryPostProcessor flywayJpaDependency() {
        return new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
                    throws BeansException {
                if (!(beanFactory instanceof BeanDefinitionRegistry registry)) {
                    return;
                }
                for (final String name : beanFactory.getBeanNamesForType(EntityManagerFactory.class,
                        true, false)) {
                    final var bd = registry.getBeanDefinition(name);
                    final var current = bd.getDependsOn();
                    if (current == null) {
                        bd.setDependsOn("flyway");
                    } else {
                        final var updated = new String[current.length + 1];
                        System.arraycopy(current, 0, updated, 0, current.length);
                        updated[current.length] = "flyway";
                        bd.setDependsOn(updated);
                    }
                }
            }
        };
    }
}
