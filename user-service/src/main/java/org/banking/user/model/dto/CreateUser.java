package org.banking.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUser {

    private String firstName;

    private String lastName;

    private String contactNumber;

    private String emailId;

    private String password;

}
