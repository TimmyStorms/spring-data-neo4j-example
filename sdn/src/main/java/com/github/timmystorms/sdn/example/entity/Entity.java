package com.github.timmystorms.sdn.example.entity;

import org.springframework.data.neo4j.annotation.GraphId;

public abstract class Entity {

    @GraphId
    private Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
    
}
