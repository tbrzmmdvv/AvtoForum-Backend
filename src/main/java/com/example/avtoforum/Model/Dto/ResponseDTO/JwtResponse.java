package com.example.avtoforum.Model.Dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String profilePicture;

    public JwtResponse(String token, Long id, String username, String email, List<String> roles, String profilePicture) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.profilePicture = profilePicture;
    }
}
