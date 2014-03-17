package com.github.timmystorms.sdn.example.entity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

@RelationshipEntity(type = Vote.TYPE)
public class Vote extends Entity {
    
    public static final String TYPE = "LIKES";

    @StartNode
    private Person person;

    @EndNode
    private Post post;

    @Min(0)
    @Max(5)
    private int score;

    public Vote() {
    }
    
    public Vote(final Person person, final Post post, final int score) {
        this.person = person;
        this.post = post;
        this.score = score;
    }

    public Vote(final int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
