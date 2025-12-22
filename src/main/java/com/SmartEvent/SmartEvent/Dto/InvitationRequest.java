package com.SmartEvent.SmartEvent.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequest {
    private String eventId;
    private String email;
    private String firstName;
    private String lastName;
}
