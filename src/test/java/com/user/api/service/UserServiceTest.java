package com.user.api.service;

import com.user.api.exceptions.ResourceNotFound;
import com.user.api.model.User;
import com.user.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {

        user = new User();
        user.setId(1L);
        user.setFirstname("Andres");
        user.setLastname("Ramirez");
        user.setFirstname("andres@gmail.com");

    }

    @Test
    void saveStudentTest() {

        given(userRepository.save(user)).willReturn(user);

        User savedUser = userService.registerUser(user);


        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);
    }


    @Test
    void getAllUsersPositiveScenario(){

        //given
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstname("Felipe");
        user2.setLastname("Pardo");
        user2.setEmail("felipe@gmail.com");

        //when
        given(userRepository.findAll()).willReturn(List.of(user, user2));

        //then
        assertThat(userService.getAllUsers().size()).isEqualTo(2);

    }

    @Test
    void getAllUsersNegativeScenario(){

        //given
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstname("Felipe");
        user2.setLastname("Pardo");
        user2.setEmail("felipe@gmail.com");

        //when
        given(userRepository.findAll()).willReturn(new ArrayList<>());

        //then
        assertThat(userService.getAllUsers().isEmpty());
    }

    @Test
    void getUserByIdTest(){

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertThat(foundUser).isNotNull();

    }

    @Test
    void getUserByIdExceptionTest(){

        //given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(ResourceNotFound.class, () -> userService.getUserById(1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    void updateUserPositiveScenarioTest(){

        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        user.setEmail("andrespardo@gmail.com");
        user.setLastname("Pardo");

        //when
        User updatedUser = userService.updateUser(user.getId(), user);

        //then
        assertThat(updatedUser.getLastname()).isEqualTo("Pardo");
        assertThat(updatedUser.getEmail()).isEqualTo("andrespardo@gmail.com");
    }

    @Test
    void updateUserNegativeScenarioTest(){

        //given
        given(userRepository.findById(5L)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(ResourceNotFound.class, () -> userService.getUserById(5L));

        //then
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    void deleteUserTest(){

        //given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        willDoNothing().given(userRepository).delete(user);

        //when
        userService.deleteUser(1L);

        //then
        verify(userRepository, times(1)).delete(user);
    }


}
