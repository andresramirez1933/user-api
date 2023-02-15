package com.user.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.api.model.User;
import com.user.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserControllerITests{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;




    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }


   @Test
    public void registerUserTest() throws Exception{

        //given
        User user = new User();
        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");


        //when
       ResultActions response = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        //then
       response.andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstname",is(user.getFirstname())))
               .andExpect(jsonPath("$.lastname", is(user.getLastname())))
               .andExpect(jsonPath("$.email", is(user.getEmail())))
               .andDo(print());
    }

    @Test
    void getUsersTest() throws Exception {


        //given
        User user = new User();
        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");

        User user1 = new User();
        user1.setFirstname("Andres");
        user1.setLastname("Pardo");
        user1.setEmail("pardo@gmail.com");
        userRepository.saveAll(List.of(user, user1));


        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/user"));

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

    }

    @Test
    void getUserById() throws Exception {

        //given
        User user = new User();
        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");
        userRepository.save(user);

        //when
        ResultActions response = mockMvc.perform(get("/user/{id}", user.getId()));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.firstname", is(user.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user.getLastname())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

    }

    @Test
    void getUserByInvalidId() throws Exception{

        //given
        Long userId = 1L;

        //when
        ResultActions response = mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isNotFound());

        //then
        response.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    void updateStudent() throws Exception{

        //given
        User user = new User();
        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");
        userRepository.save(user);

        User updatedUser = new User("Luisa", "Ramirez", "carla@gmail.com");



        //when
        ResultActions response = mockMvc.perform(put("/user/{id}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));


        //Then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Luisa")))
                .andExpect(jsonPath("$.lastname", is("Ramirez")))
                .andExpect(jsonPath("$.email", is("carla@gmail.com")));
    }


    @Test
    void deleteUserTest() throws Exception{

        //given
        User user = new User();
        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");
        userRepository.save(user);


        //When
        ResultActions response = mockMvc.perform(delete("/user/{id}",user.getId()));

        //Then
        response.andDo(print())
                .andExpect(status().isOk());

    }

}
