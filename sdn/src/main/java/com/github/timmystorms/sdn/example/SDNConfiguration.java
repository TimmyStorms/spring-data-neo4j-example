package com.github.timmystorms.sdn.example;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.neo4j.aspects.config.Neo4jAspectConfiguration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.fieldaccess.ConvertingNodePropertyFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.DelegatingFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.FieldAccessListener;
import org.springframework.data.neo4j.fieldaccess.FieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.FieldAccessorFactoryFactory;
import org.springframework.data.neo4j.fieldaccess.FieldAccessorListenerFactory;
import org.springframework.data.neo4j.fieldaccess.IdFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.IndexingPropertyFieldAccessorListenerFactory;
import org.springframework.data.neo4j.fieldaccess.PropertyFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.RelationshipNodeFieldAccessorFactory;
import org.springframework.data.neo4j.fieldaccess.TransientFieldAccessorFactory;
import org.springframework.data.neo4j.mapping.Neo4jPersistentEntity;
import org.springframework.data.neo4j.mapping.Neo4jPersistentProperty;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableAspectJAutoProxy
@EnableSpringConfigured
@ComponentScan("com.github.timmystorms.sdn.example")
@EnableNeo4jRepositories(basePackages = { "com.github.timmystorms.sdn.example.repository" })
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class SDNConfiguration extends Neo4jAspectConfiguration {
    
    public SDNConfiguration() {
        setBasePackage("com.github.timmystorms.sdn.example.entity");
    }

    @Bean(destroyMethod = "shutdown")
    public GraphDatabaseService graphDatabaseService() {
        return new GraphDatabaseFactory().newEmbeddedDatabase("db");
    }

    @Bean
    public ExecutionEngine executionEngine() {
        return new ExecutionEngine(graphDatabaseService());
    }
    
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
   
    /* BEGIN workaround DATAGRAPH-451 */
    
    @Bean
    public FieldAccessorFactoryFactory relationshipDelegatingFieldAccessorFactory() throws Exception {
        return new RelationshipDelegatingFieldAccessorFactory.Factory();
    }
    
   
    static class RelationshipDelegatingFieldAccessorFactory extends DelegatingFieldAccessorFactory {
        public RelationshipDelegatingFieldAccessorFactory(Neo4jTemplate template) {
            super(template);
        }

        @Override
        protected Collection<FieldAccessorListenerFactory> createListenerFactories() {
            return Arrays.<FieldAccessorListenerFactory>asList(
                    new IndexingPropertyFieldAccessorListenerFactory(
                            template,
                            new PropertyFieldAccessorFactory(template),
                            new ConvertingNodePropertyFieldAccessorFactory(template)
                            /* BUGFIX: add a ValidatingNodePropertyFieldAccessorListenerFactory for relationships */
                    ), new ValidatingNodePropertyFieldAccessorListenerFactory(template));
        }

        @Override
        protected Collection<? extends FieldAccessorFactory> createAccessorFactories() {
            return Arrays.<FieldAccessorFactory>asList(
                    new TransientFieldAccessorFactory(),
                    new IdFieldAccessorFactory(template),
                    new RelationshipNodeFieldAccessorFactory(template),
                    new PropertyFieldAccessorFactory(template),
                    new ConvertingNodePropertyFieldAccessorFactory(template),
                    new DynamicPropertiesFieldAccessorFactory(template)
            );
        }

        public static class Factory extends FieldAccessorFactoryFactory {
            @Override
            public DelegatingFieldAccessorFactory create(Neo4jTemplate template) {
                return new RelationshipDelegatingFieldAccessorFactory(template);
            }
        }
    }
    
    static class ValidatingNodePropertyFieldAccessorListenerFactory implements FieldAccessorListenerFactory {

        private final Neo4jTemplate template;

        ValidatingNodePropertyFieldAccessorListenerFactory(final Neo4jTemplate template) {
            this.template = template;
        }

        @Override
        public boolean accept(final Neo4jPersistentProperty property) {
            return hasValidationAnnotation(property);
        }

        private boolean hasValidationAnnotation(final Neo4jPersistentProperty property) {
            for (Annotation annotation : property.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(Constraint.class)) return true;
            }
            return false;
        }

        @Override
        public FieldAccessListener forField(Neo4jPersistentProperty property) {
            return new ValidatingNodePropertyFieldAccessorListener(property, template.getValidator());
        }


        /**
         * @author Michael Hunger
         * @since 12.09.2010
         */
        public static class ValidatingNodePropertyFieldAccessorListener<T extends PropertyContainer> implements FieldAccessListener {

            private final static Logger log = LoggerFactory.getLogger(ValidatingNodePropertyFieldAccessorListener.class);
            private String propertyName;
            private Validator validator;
            private Neo4jPersistentEntity<?> entityType;

            public ValidatingNodePropertyFieldAccessorListener(final Neo4jPersistentProperty field, Validator validator) {
                this.propertyName = field.getName();
                this.entityType = field.getOwner();
                this.validator = validator;
            }

            @Override
            public void valueChanged(Object entity, Object oldVal, Object newVal) {
                if (validator==null) return;
                @SuppressWarnings("unchecked") Class<T> type = (Class<T>) entityType.getType();
                Set<ConstraintViolation<T>> constraintViolations = validator.validateValue(type, propertyName, newVal);
                if (!constraintViolations.isEmpty()) throw new ValidationException("Error validating field "+propertyName+ " of "+entityType+": "+constraintViolations);
            }
        }
    }
    
    /* END workaround DATAGRAPH-451 */
    
}
