package com.github.timmystorms.sdn.example.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.github.timmystorms.sdn.example.entity.Vote;

@Repository
public interface VoteRepository extends GraphRepository<Vote> {

}
