package com.github.timmystorms.sdn.example.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.github.timmystorms.sdn.example.entity.Person;

@RepositoryRestResource(collectionResourceRel = "persons", path = "person")
@Repository
public interface PersonRepository extends GraphRepository<Person> {

    @Query("MATCH (p:Person {firstName:{pname}}) RETURN p")
    Set<Person> findByName(@Param("pname") String name);
    
    Set<Person> findByFirstName(String firstName);
    
}
