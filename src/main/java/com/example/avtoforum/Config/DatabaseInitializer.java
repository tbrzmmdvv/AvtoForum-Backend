package com.example.avtoforum.Config;

import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Model.Entity.User; // User modelini import edin
import com.example.avtoforum.Repository.RoleRepository;
import com.example.avtoforum.Repository.UserRepository; // UserRepository'i import edin
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder'ı import edin
import org.springframework.transaction.annotation.Transactional; // Transactional import edin

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseInitializer {

    @Bean
    @Transactional // Veritabanı işlemlerinin bütünlüğü için
    public CommandLineRunner initDatabase(RoleRepository roleRepository,
                                          UserRepository userRepository,      // UserRepository'i inject edin
                                          PasswordEncoder passwordEncoder) { // PasswordEncoder'ı inject edin
        return args -> {
            System.out.println("DatabaseInitializer: Checking and setting up roles and admin user...");

            // 1. Gerekli rollerin var olduğundan emin ol (Admin ve User rolleri en azından)
            // Role.ERole.ROLE_ADMIN gibi enum yolunun doğru olduğundan emin olun.
            // Projenizdeki ERole enum'ının tam yolu farklıysa (örn: com.example.avtoforum.Model.Enum.ERole), onu kullanın.
            Role adminRoleEntity = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    System.out.println("DatabaseInitializer: ROLE_ADMIN not found, creating...");
                    Role newAdminRole = new Role();
                    newAdminRole.setName(Role.ERole.ROLE_ADMIN);
                    return roleRepository.save(newAdminRole);
                });

            Role userRoleEntity = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseGet(() -> {
                    System.out.println("DatabaseInitializer: ROLE_USER not found, creating...");
                    Role newUserRole = new Role();
                    newUserRole.setName(Role.ERole.ROLE_USER);
                    return roleRepository.save(newUserRole);
                });
            
            // Moderator rolünü de isterseniz burada benzer şekilde kontrol edebilirsiniz
            // Bu, orijinal kodunuzda olduğu için buraya da ekliyorum:
            roleRepository.findByName(Role.ERole.ROLE_MODERATOR)
                .orElseGet(() -> {
                    System.out.println("DatabaseInitializer: ROLE_MODERATOR not found, creating...");
                    Role newModRole = new Role();
                    newModRole.setName(Role.ERole.ROLE_MODERATOR);
                    return roleRepository.save(newModRole);
                });

            System.out.println("DatabaseInitializer: Essential roles checked/created.");

            // 2. 'tbrzmmdvv' kullanıcısını admin yap
            String adminUsername = "tbrzmmdvv";
            String adminEmail = "YOUR_ACTUAL_EMAIL@example.com"; // LÜTFEN KENDİ GEÇERLİ E-POSTANIZLA DEĞİŞTİRİN!
            String adminPassword = "YOUR_VERY_STRONG_PASSWORD_HERE!"; // LÜTFEN ÇOK GÜVENLİ BİR ŞİFREYLE DEĞİŞTİRİN!

            Optional<User> existingUserOpt = userRepository.findByUsername(adminUsername);

            User userToMakeAdmin;
            if (existingUserOpt.isEmpty()) {
                // Kullanıcı yok, yeni bir tane oluştur ve admin yap
                System.out.println("DatabaseInitializer: User '" + adminUsername + "' not found. Creating new user with admin role.");
                userToMakeAdmin = new User();
                userToMakeAdmin.setUsername(adminUsername);
                userToMakeAdmin.setEmail(adminEmail); 
                userToMakeAdmin.setPassword(passwordEncoder.encode(adminPassword));

                Set<Role> roles = new HashSet<>();
                roles.add(adminRoleEntity); // Admin rolünü ekle
                roles.add(userRoleEntity);  // Adminler genellikle kullanıcı rolüne de sahip olur
                userToMakeAdmin.setRoles(roles);
                // Modelinize göre diğer zorunlu alanlar varsa burada set edin (örn: userToMakeAdmin.setEnabled(true);)
                userRepository.save(userToMakeAdmin);
                System.out.println("DatabaseInitializer: User '" + adminUsername + "' created and granted ROLE_ADMIN.");
            } else {
                // Kullanıcı var, admin rolü var mı kontrol et, yoksa ekle
                userToMakeAdmin = existingUserOpt.get();
                System.out.println("DatabaseInitializer: User '" + adminUsername + "' found. Checking for ROLE_ADMIN...");

                boolean hasAdminRole = false;
                if (userToMakeAdmin.getRoles() != null) { // Null check
                    hasAdminRole = userToMakeAdmin.getRoles().stream()
                                        .anyMatch(role -> role.getName().equals(Role.ERole.ROLE_ADMIN));
                }


                if (!hasAdminRole) {
                    if (userToMakeAdmin.getRoles() == null) { // Eğer roller null ise yeni bir set oluştur
                        userToMakeAdmin.setRoles(new HashSet<>());
                    }
                    userToMakeAdmin.getRoles().add(adminRoleEntity);
                    userRepository.save(userToMakeAdmin);
                    System.out.println("DatabaseInitializer: ROLE_ADMIN granted to existing user '" + adminUsername + "'.");
                } else {
                    System.out.println("DatabaseInitializer: User '" + adminUsername + "' already has ROLE_ADMIN.");
                }
            }
            System.out.println("DatabaseInitializer: Admin user setup process finished.");
        };
    }
}
