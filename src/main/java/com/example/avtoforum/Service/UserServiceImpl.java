package com.example.avtoforum.Service;

import com.example.avtoforum.Exception.ResourceNotFoundException;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.User;
import com.example.avtoforum.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value("${file.upload-dir}")
    private String uploadDir;


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public MessageResponse uploadProfilePicture(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        try {
            // Dosya adını benzersiz hale getir
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Yükleme klasörü yolunu oluştur
            Path uploadPath = Paths.get(uploadDir);

            // Klasör yoksa oluştur
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Dosyayı kopyala
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Kullanıcı profil resmini güncelle
            String fileUrl = "/images/" + fileName;
            user.setProfilePicture(fileUrl);
            userRepository.save(user);

            return new MessageResponse("Profil resmi başarıyla yüklendi. URL: " + fileUrl);

        } catch (IOException ex) {
            throw new RuntimeException("Dosya yüklenirken bir hata oluştu", ex);
        }
    }
}
