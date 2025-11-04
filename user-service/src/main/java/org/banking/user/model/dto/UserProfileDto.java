package org.banking.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private String firstName;

    private String lastName;

    private String gender;

    private String address;

    private String contactNo;

    private String occupation;

    private String martialStatus;

    private String nationality;

}
