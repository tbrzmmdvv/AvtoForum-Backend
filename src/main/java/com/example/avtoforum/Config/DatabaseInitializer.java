package com.example.avtoforum.Config;

import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Rolleri kontrol et ve yoksa oluştur
            if (roleRepository.count() == 0) {
                System.out.println("Roller oluşturuluyor...");

                Role userRole = new Role();
                userRole.setName(Role.ERole.ROLE_USER);
                roleRepository.save(userRole);

                Role modRole = new Role();
                modRole.setName(Role.ERole.ROLE_MODERATOR);
                roleRepository.save(modRole);

                Role adminRole = new Role();
                adminRole.setName(Role.ERole.ROLE_ADMIN);
                roleRepository.save(adminRole);

                System.out.println("Roller başarıyla oluşturuldu!");
            } else {
                System.out.println("Roller zaten mevcut. İşlem yapılmadı.");
            }
        };
    }
}
