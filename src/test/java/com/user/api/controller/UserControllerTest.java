package com.user.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.api.exceptions.ResourceNotFound;
import com.user.api.model.User;
import com.user.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setup(){

        user = new User();
        user.setId(1L);
        user.setFirstname("Andres");
        user.setLastname("Ramirez");
        user.setEmail("andres@gmail.com");
    }

    @Test
    void registerUserTest() throws Exception {

        //given
        //it returns whatever the argument that we pass to the controller method
        //given(userService.registerUser(any(         )))
          //      .willAnswer((invocation)-> invocation.getArgument(0));

        given(userService.registerUser(any(User.class)))
                        .willReturn(user);

        //when
        ResultActions response = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        //then
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname",
                        is(user.getFirstname())))
                .andExpect(jsonPath("$.lastname",
                        is(user.getLastname())))
                .andExpect(jsonPath("$.email",
                        is(user.getEmail())));
    }

    @Test
    void getUsersTest() throws Exception {

        //given
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstname("Felipe");
        user2.setLastname("Pardo");
        user2.setEmail("felipe@gmail.com");
        List<User> users = List.of(user, user2);

        given(userService.getAllUsers()).willReturn(users);

        //when
        ResultActions response = mockMvc.perform(get("/user"));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getUserByIdPositiveScenario() throws Exception {

        //given
        given(userService.getUserById(any())).willReturn(user);

        //when
        ResultActions response = mockMvc.perform(get("/user/{id}", user.getId()));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is(user.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user.getLastname())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getUserByIdNegativeScenario() throws Exception {

        //given
        given(userService.getUserById(any())).willThrow(ResourceNotFound.class);

        //when
        ResultActions response = mockMvc.perform(get("/user/{id}", 55L));

        //then
        response.andDo(print())
                .andExpect(status().is4xxClientError());

    }

    @Test
    void updateUserTest() throws Exception {

        //given
        User user1 = new User();
        user1.setFirstname("Felipe");
        user1.setLastname("Pardo");
        user1.setEmail("felipe@gmail.com");

        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.updateUser(any(), any(User.class))).willAnswer(invocation -> invocation.getArgument(1));

        //when
        ResultActions response = mockMvc.perform(put("/user/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));


        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is(user1.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(user1.getLastname())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));

    }

    @Test
    void deleteUserTest() throws Exception {

        //given
        willDoNothing().given(userService).deleteUser(user.getId());

        //when
        ResultActions response = mockMvc.perform(delete("/user/{id}", user.getId()));

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }






}
