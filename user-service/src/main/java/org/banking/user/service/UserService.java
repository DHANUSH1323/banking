package org.banking.user.service;

import org.banking.user.model.dto.CreateUser;
import org.banking.user.model.dto.UserDto;
import org.banking.user.model.dto.UserUpdate;
import org.banking.user.model.dto.UserUpdateStatus;
import org.banking.user.model.dto.response.Response;

import java.util.List;

public interface UserService {
    Response createUser(CreateUser userDto);

    List<UserDto> readAllUsers();

    UserDto readUser(String authId);

    Response updateUserStatus(Long id, UserUpdateStatus userUpdateStatus);

    Response updateUser(Long id, UserUpdate userUpdate);

    UserDto readUserById(Long userId);

    UserDto readUserByAccountId(String accountId);
}
