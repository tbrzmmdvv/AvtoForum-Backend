package com.example.avtoforum.Service;

import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User getUserById(Long id);

    MessageResponse uploadProfilePicture(Long id, MultipartFile file);
}
