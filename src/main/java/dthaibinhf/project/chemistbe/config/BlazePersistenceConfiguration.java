package dthaibinhf.project.chemistbe.config;


import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import dthaibinhf.project.chemistbe.dto.view.GroupListView;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlazePersistenceConfiguration {
    private final EntityManagerFactory entityManagerFactory;

    public BlazePersistenceConfiguration(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    public EntityViewConfiguration createEntityViewConfiguration() {
        EntityViewConfiguration config = EntityViews.createDefaultConfiguration();
        config.addEntityView(GroupListView.class); // Register your entity view classes
        // Add other configurations as needed
        return config;
    }

    @Bean
    public EntityViewManager createEntityViewManager(
            CriteriaBuilderFactory criteriaBuilderFactory, EntityViewConfiguration entityViewConfiguration) {
        return entityViewConfiguration.createEntityViewManager(criteriaBuilderFactory);
    }
}
