package com.github.timmystorms.sdn.example;

import javax.validation.Validator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.neo4j.aspects.config.Neo4jAspectConfiguration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
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
    
}
