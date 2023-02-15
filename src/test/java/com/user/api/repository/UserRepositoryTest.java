package com.user.api.repository;

import com.user.api.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup(){

        user = new User();
        user.setFirstname("Andres");
        user.setLastname("Ramirez");
        user.setEmail("andres@gmail.com");


    }


    //JUnit test to save employee operation
    @Test
    public void registerUserTest() {

        //when - action or the behavior that we are going test
        User savedUser = userRepository.save(user);

        // then - verify the output
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);

    }

    @Test
    public void getUsersTest(){

        User user1 = new User();
        user1.setFirstname("Felipe");
        user1.setLastname("Pardo");
        user1.setEmail("felipe@gmail.com");

        userRepository.saveAll(List.of(user, user1));

        //when - action or the behavior that we are going test
        List<User> users = userRepository.findAll();

        //then - action or the behavior that we are going to test
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void getUserByIdTest(){

        userRepository.save(user);

        //when - action or the behavior that we are going to test
        User foundUser = userRepository.findById(user.getId()).get();

        //then - action or the behavior that we are going to test

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());

    }

    @Test
    public void getUserByEmailTest(){


        userRepository.save(user);
        //when - action or the behavior that we are going to test
        User foundUser = userRepository.findByEmail(user.getEmail()).get();

        //then - action or the behavior that we are going to test

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void updateUser(){

        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).get();
        foundUser.setEmail("felipe@gmail.com");
        User updatedUser = userRepository.save(foundUser);

        assertThat(foundUser.getEmail()).isEqualTo("felipe@gmail.com");

    }

    @Test
    public void deleteUser(){

        userRepository.save(user);
        userRepository.delete(user);

        Optional<User> userOptional = userRepository.findById(user.getId());

        assertThat(userOptional).isEmpty();
    }
}
