package com.user.api;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

    @Value("${sql.script.delete.user}")
    private String sqlDeleteUsers;

    @Value("${sql.script.alter.column.id}")
    private String sqlUpdateUserId;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setup(){

        User user = new User("Andres", "Ramirez", "andres@gmail.com");
        User user1 = new User("Gloria", "Pardo", "gloria@gmail.com");
        User user2 = new User("Claudia", "Rodriguez", "claudia@gmail.com");

        userRepository.saveAll(List.of(user, user1, user2));


    }

    @Test
    void getStudentsTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)));


    }

   @Test
    public void registerUserTest() throws Exception{

        User user1 = new User();
        user1.setFirstname("Luisa");
        user1.setLastname("Ramirez");
        user1.setEmail("luisa@gmail.com");


        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname",is(user1.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user1.getLastname())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

        userRepository.findAll().forEach(user -> System.out.println(user.getId() + " " +  user.getFirstname()));

    }

    @Test
    void deleteUserTest() throws Exception{

        assertTrue(userRepository.findById(1).isPresent());

        mockMvc.perform(delete("/user/{id}",1))
                .andExpect(status().isOk());

        assertFalse(userRepository.findById(1).isPresent());

    }

    @Test
    void getStudentById() throws Exception{


        Optional<User> user = userRepository.findById(1);

        assertTrue(user.isPresent());

        mockMvc.perform(get("/user/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", is(user.get().getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user.get().getLastname())))
                .andExpect(jsonPath("$.email", is(user.get().getEmail())));
    }

    @Test
    void getStudentByInvalidId() throws Exception{

        assertFalse(userRepository.findById(55).isPresent());

        mockMvc.perform(get("/user/{id}", 55))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/user/{id}", 55))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    void updateStudent() throws Exception{

        User updatedUser = new User("Carlos", "Mendez", "carlos@gmail.com");

        mockMvc.perform(put("/user/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Carlos")))
                .andExpect(jsonPath("$.lastname", is("Mendez")))
                .andExpect(jsonPath("$.email", is("carlos@gmail.com")));
    }

    @Test
    void updateStudentNotFound() throws Exception{

        User updatedUser = new User("Carlos", "Mendez", "carlos@gmail.com");

        mockMvc.perform(put("/user/{id}",55)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @AfterEach
    void setupAfterEach(){

        jdbc.execute(sqlDeleteUsers);
        jdbc.execute(sqlUpdateUserId);

    }



}
