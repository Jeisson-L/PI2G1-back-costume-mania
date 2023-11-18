package com.costumemania.msusers.service.implementation;

import com.costumemania.msusers.model.dto.CreateUserRequest;
import com.costumemania.msusers.model.dto.UpdateUserRequest;
import com.costumemania.msusers.model.dto.UserAccountResponse;
import com.costumemania.msusers.model.entity.UserEntity;
import com.costumemania.msusers.repository.IUserRepository;
import com.costumemania.msusers.service.IUserService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements IUserService {

    private IUserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImplementation(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserAccountResponse createUser(CreateUserRequest user) {

        UserEntity userEntity = CreateUserRequest.toUserEntity(user);

        userEntity = save(userEntity);

        UserAccountResponse userResponse = UserAccountResponse.fromUserEntity(userEntity);

        return userResponse;
    }

    @Override
    public UserAccountResponse getByUsername(String username) {
        Optional<UserEntity> userExists = userRepository.findByUsername(username);
        if (userExists.isEmpty()) throw new NotFoundException(String.format("Username: %s Not found", username));

        UserAccountResponse userResponse = UserAccountResponse.fromUserEntity(userExists.get());
        return userResponse;
    }

    @Override
    public UserAccountResponse getById(int id) {
        Optional<UserEntity> userExists = userRepository.findById(id);
        if (userExists.isEmpty()) throw new NotFoundException(String.format("User id: %s Not found", id));

        UserAccountResponse userResponse = UserAccountResponse.fromUserEntity(userExists.get());
        return userResponse;
    }

    @Override
    public UserAccountResponse getByDni(String dni) {

        Optional<UserEntity> userExists = userRepository.findByDni(dni);
        if (userExists.isEmpty()) throw new NotFoundException(String.format("User dni: %s Not found", dni));

        UserAccountResponse userResponse = UserAccountResponse.fromUserEntity(userExists.get());
        return userResponse;
    }

    @Override
    public Set<UserAccountResponse> getAllUsers() {
        Set<UserAccountResponse> setUsers = userRepository.findAll().stream()
                .map(user -> UserAccountResponse.fromUserEntity(user)).collect(Collectors.toSet());
        return setUsers;
    }

    @Override
    public void deleteUserById(int id) {

        userRepository.deleteById(id);
    }

    @Override
    public UserAccountResponse updateUserFromUser(UpdateUserRequest user) {

        UserAccountResponse foundUser = getById(user.getId());

        UserEntity updateUser = UpdateUserRequest.toUserEntity(user);
        updateUser.setCreatedAt(foundUser.getCreatedAt());

        updateUser = save(updateUser);

        UserAccountResponse response = UserAccountResponse.fromUserEntity(updateUser);

        return response;
    }

    //Common method to be used for create and update
    private UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
