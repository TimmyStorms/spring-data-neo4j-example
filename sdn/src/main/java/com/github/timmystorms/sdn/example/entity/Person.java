package com.github.timmystorms.sdn.example.entity;

import java.util.Set;

import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Person extends Entity {

    @NotBlank
    @Indexed
    private String firstName;

    @NotBlank
    @Indexed
    private String lastName;

    @RelatedToVia
    private Set<Vote> votes;
    
    @Max(100)
    private int age;

    public Person() {
    }

    public Person(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(final Set<Vote> votes) {
        this.votes = votes;
    }

    public Vote likes(final Post post, final int score) {
        final Vote vote = this.relateTo(post, Vote.class, Vote.TYPE);
        vote.setScore(score);
        return vote;
    }

    public void dislike(final Post post) {
        removeRelationshipTo(post, Vote.TYPE);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
