package com.github.timmystorms.sdn.example;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.github.timmystorms.sdn.example.entity.Person;
import com.github.timmystorms.sdn.example.entity.Post;
import com.github.timmystorms.sdn.example.repository.PersonRepository;
import com.github.timmystorms.sdn.example.repository.PostRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SDNConfiguration.class, SDNTest.TestConfiguration.class })
@Transactional
public class SDNTest {

    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private PostRepository postRepo;

    static class TestConfiguration {

        @Bean(destroyMethod = "shutdown")
        public GraphDatabaseService graphDatabaseService() {
            return new TestGraphDatabaseFactory().newImpermanentDatabase();
        }

    }

    @Test
    public void testPerson() {
        final Person john = new Person("John", "Doe").persist();
        final Person foo = new Person("Foo", "Bar").persist();
        Assert.assertEquals(2, personRepo.count());
        Assert.assertEquals(john, personRepo.findByName("John").iterator().next());
        Assert.assertEquals(foo, personRepo.findByFirstName("Foo").iterator().next());
        Assert.assertNotNull(personRepo.query("MATCH (p:Person) WHERE p.firstName =~ '.*oh.*' RETURN p", null).single());
        final Post post = new Post("SDN Example", "Description").persist();
        john.likes(post, 5);
        Assert.assertEquals(5, personRepo.findByName("John").iterator().next().getVotes().iterator().next().getScore());
    }

    @Test(expected = ValidationException.class)
    public void failPersonValidation() {
        new Person("", null).persist();
    }
    
    @Test(expected = ValidationException.class)
    public void failAgeValidation() {
        final Person john = new Person("John", "Doe");
        john.setAge(150);
        john.persist();
    }
    
    @Test(expected = ValidationException.class)
    public void failScoreValidation() {
        final Person john = new Person("John", "Doe").persist();
        final Post post = new Post("SDN Example", "Description").persist();
        john.likes(post, 10);
    }

    @Test
    public void testPost() {
        final Post post = new Post("SDN Example", "Description").persist();
        Assert.assertEquals(post, postRepo.findByTitle("SDN Example").iterator().next());
        final Person john = new Person("John", "Doe").persist();
        john.likes(post, 2);
        Assert.assertEquals(1, IteratorUtil.count(postRepo.findVoters(post)));
    }

}
