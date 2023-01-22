package com.user.api.service;

import com.user.api.exceptions.ResourceNotFound;
import com.user.api.model.User;
import com.user.api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public List<User> getAllUsers(){

        Iterable<User> source =userRepository.findAll();
        List<User> target = new ArrayList<>();
        source.forEach(target::add);



        return target;
    }

    public User registerUser(User user){

        User savedUser = userRepository.save(user);





        return savedUser;
    }

    public User getUserById(int id){

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found"));

        return user;
    }

    public User updateUser(int id, User user){

        User userFound = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found"));

        userFound.setFirstname(user.getFirstname());
        userFound.setLastname(user.getLastname());
        userFound.setEmail(user.getEmail());

        User userUpdated = userRepository.save(userFound);

        return userUpdated;
    }

    public void deleteUser(int id){

        User userFound = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found"));

        userRepository.delete(userFound);
    }

}
