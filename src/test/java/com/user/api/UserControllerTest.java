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

    @Value("${sql.script.insert.user1}")
    private String sqlInsertUser1;

    @Value("${sql.script.insert.user2}")
    private String sqlInsertUser2;
    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private User user;

    @BeforeEach
    void setup(){

        jdbc.execute(sqlInsertUser1);
        jdbc.execute(sqlInsertUser2);

    }

    @Test
    void getStudentsTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));


        userRepository.findAll().forEach(user -> System.out.println(user.getId() + " " +  user.getFirstname()));

    }

   @Test
    public void registerUserTest() throws Exception{

        user.setFirstname("Luisa");
        user.setLastname("Ramirez");
        user.setEmail("luisa@gmail.com");


        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname",is(user.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user.getLastname())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        userRepository.findAll().forEach(user -> System.out.println(user.getId() + " " +  user.getFirstname()));

    }

    @Test
    void deleteUserTest() throws Exception{

        List<User> users = userRepository.findAll();

        assertNotNull(users.get(0));
        int id = users.get(0).getId();

        mockMvc.perform(delete("/user/{id}",id))
                .andExpect(status().isOk());

        assertFalse(userRepository.findById(id).isPresent());

    }

    @Test
    void getStudentById() throws Exception{

        List<User> users = userRepository.findAll();

        assertNotNull(users.get(0));
        User user = users.get(0);

        assertTrue(userRepository.findById(user.getId()).isPresent());

        mockMvc.perform(get("/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", is(user.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user.getLastname())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
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

        List<User> users = userRepository.findAll();

        assertNotNull(users.get(0));
        User user = users.get(0);

        mockMvc.perform(put("/user/{id}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
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
    }



}
