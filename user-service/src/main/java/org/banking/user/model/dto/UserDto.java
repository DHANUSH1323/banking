package org.banking.user.model.dto;

import org.banking.user.model.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;

    private String emailId;

    private String password;

    private String identificationNumber;

    private String authId;

    private Status status;

    private UserProfileDto userProfileDto;

}
