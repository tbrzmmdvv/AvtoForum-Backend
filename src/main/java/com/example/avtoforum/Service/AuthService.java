package com.example.avtoforum.Service;

import com.example.avtoforum.Model.Dto.RequestDTO.LoginRequest;
import com.example.avtoforum.Model.Dto.RequestDTO.RegisterRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.JwtResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;

public interface AuthService {

    JwtResponse authenticateUser(LoginRequest loginRequest);

    MessageResponse registerUser(RegisterRequest registerRequest);
}
