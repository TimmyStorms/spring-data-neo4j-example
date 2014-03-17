package com.github.timmystorms.sdn.example.repository;

import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.github.timmystorms.sdn.example.entity.Person;
import com.github.timmystorms.sdn.example.entity.Post;

@Repository
public interface PostRepository extends GraphRepository<Post> {

    Set<Post> findByTitle(String title);
    
    @Query("START post=node({0}) MATCH (person:Person)-[:LIKES]->(post) RETURN person")
    Set<Person> findVoters(Post post);
    
}
