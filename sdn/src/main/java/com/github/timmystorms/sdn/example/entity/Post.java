package com.github.timmystorms.sdn.example.entity;

import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedToVia;

@NodeEntity
public class Post extends Entity {

    @NotBlank
    @Indexed
    private String title;

    @NotBlank
    @Indexed
    private String content;

    @RelatedToVia
    private Set<Vote> votes;

    public Post() {
    }

    public Post(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(final Set<Vote> votes) {
        this.votes = votes;
    }

}
