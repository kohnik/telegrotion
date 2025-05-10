package com.fizzly.backend.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Size(min = 5, max = 50)
    @NotBlank
    @JsonProperty("username")
    private String username;

    @Size(max = 255)
    @JsonProperty("password")
    private String password;

}