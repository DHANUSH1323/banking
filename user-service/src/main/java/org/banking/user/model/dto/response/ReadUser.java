package org.banking.user.model.dto.response;

import org.banking.user.model.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadUser {

    private String firstName;

    private String lastName;

    private String emailId;

    private String contactNumber;

    private Status status;
}
