package com.example.avtoforum.Config; // Paket adınızın doğru olduğundan emin olun

import com.example.avtoforum.Model.Entity.Role; // Role entity'nizin tam yolu
import com.example.avtoforum.Model.Entity.User; // User entity'nizin tam yolu
import com.example.avtoforum.Repository.RoleRepository;
import com.example.avtoforum.Repository.UserRepository; // UserRepository'i eklemeniz gerekecek
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder'ı eklemeniz gerekecek
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseInitializer {

    @Bean
    @Transactional // Birden fazla DB işlemi (rol ve kullanıcı) olduğu için transactional yapmak iyi bir pratiktir.
    public CommandLineRunner initDatabase(RoleRepository roleRepository,
                                          UserRepository userRepository, // UserRepository'i inject edin
                                          PasswordEncoder passwordEncoder) { // PasswordEncoder'ı inject edin
        return args -> {
            System.out.println("DatabaseInitializer: Running initialization process...");

            // 1. Rolleri kontrol et ve yoksa oluştur
            Role userRoleEntity; // Entity referanslarını CommandLineRunner scope'unda tanımla
            Role adminRoleEntity;
            // Role moderatorRoleEntity; // Eğer moderatör rolünü de kullanacaksanız

            // ERole enum'ınızın tam yolunu kontrol edin, örneğin: Role.ERole.ROLE_USER
            // Eğer Role sınıfınızda ERole enum'ı iç içe değilse, direkt ERole.ROLE_USER şeklinde kullanın.
            // Bu örnekte Role.ERole.XXX kullanıldığı varsayılmıştır.

            Optional<Role> userRoleOpt = roleRepository.findByName(com.example.avtoforum.Model.Enum.ERole.ROLE_USER); // ERole enum'ınızın tam yolunu kullanın
            if (userRoleOpt.isEmpty()) {
                System.out.println("DatabaseInitializer: ROLE_USER not found, creating...");
                userRoleEntity = new Role();
                userRoleEntity.setName(com.example.avtoforum.Model.Enum.ERole.ROLE_USER); // ERole enum'ınızın tam yolunu kullanın
                roleRepository.save(userRoleEntity);
            } else {
                userRoleEntity = userRoleOpt.get();
            }

            Optional<Role> modRoleOpt = roleRepository.findByName(com.example.avtoforum.Model.Enum.ERole.ROLE_MODERATOR); // ERole enum'ınızın tam yolunu kullanın
            if (modRoleOpt.isEmpty()) {
                System.out.println("DatabaseInitializer: ROLE_MODERATOR not found, creating...");
                Role modRole = new Role();
                modRole.setName(com.example.avtoforum.Model.Enum.ERole.ROLE_MODERATOR); // ERole enum'ınızın tam yolunu kullanın
                roleRepository.save(modRole);
                // moderatorRoleEntity = modRole; // Eğer kullanacaksanız
            } else {
                // moderatorRoleEntity = modRoleOpt.get(); // Eğer kullanacaksanız
            }

            Optional<Role> adminRoleOpt = roleRepository.findByName(com.example.avtoforum.Model.Enum.ERole.ROLE_ADMIN); // ERole enum'ınızın tam yolunu kullanın
            if (adminRoleOpt.isEmpty()) {
                System.out.println("DatabaseInitializer: ROLE_ADMIN not found, creating...");
                adminRoleEntity = new Role();
                adminRoleEntity.setName(com.example.avtoforum.Model.Enum.ERole.ROLE_ADMIN); // ERole enum'ınızın tam yolunu kullanın
                roleRepository.save(adminRoleEntity);
            } else {
                adminRoleEntity = adminRoleOpt.get();
            }
            System.out.println("DatabaseInitializer: Roles checked/created.");

            // 2. Admin kullanıcısını tanımlayın ve oluşturun/güncelleyin
            String adminUsername = "tbrzmmdvv"; // Sizin GitHub kullanıcı adınız
            String adminEmail = "tebrizrza055@gmail.com"; // LÜTFEN BUNU GEÇERLİ BİR E-POSTA İLE DEĞİŞTİRİN!
            String adminPassword = "123456tr"; // LÜTFEN BUNU ÇOK GÜVENLİ BİR ŞİFRE İLE DEĞİŞTİRİN!

            Optional<User> existingAdminOpt = userRepository.findByUsername(adminUsername);

            if (existingAdminOpt.isEmpty()) {
                // Admin kullanıcısı yok, yeni bir tane oluştur
                User adminUser = new User();
                adminUser.setUsername(adminUsername);
                adminUser.setEmail(adminEmail); // E-postayı ayarla

                if (passwordEncoder == null) {
                    System.err.println("DatabaseInitializer: PasswordEncoder is null! Cannot create admin user. Ensure it's correctly configured in your Spring Security setup.");
                    throw new IllegalStateException("PasswordEncoder must be configured and injected.");
                }
                adminUser.setPassword(passwordEncoder.encode(adminPassword));

                Set<Role> roles = new HashSet<>();
                roles.add(adminRoleEntity); // Admin rolünü ekle
                if (userRoleEntity != null) { // userRoleEntity'nin null olmadığından emin olun
                    roles.add(userRoleEntity);  // Genellikle adminler kullanıcı rolüne de sahiptir
                } else {
                     System.err.println("DatabaseInitializer: userRoleEntity is null. Cannot assign ROLE_USER to admin.");
                }
                adminUser.setRoles(roles);
                // Kullanıcı için gerekli diğer alanları da burada set edebilirsiniz (isActive, createdAt vb.)
                // Örneğin: adminUser.setEnabled(true); veya adminUser.setActive(true); (Modelinize göre)

                userRepository.save(adminUser);
                System.out.println("DatabaseInitializer: Admin user '" + adminUsername + "' created successfully with ROLE_ADMIN.");
            } else {
                // Admin kullanıcısı zaten var, rollerini kontrol et ve gerekirse güncelle
                User adminToUpdate = existingAdminOpt.get();

                boolean isAdminRolePresent = false;
                if (adminToUpdate.getRoles() != null) { // Rollerin null olup olmadığını kontrol et
                    for (Role role : adminToUpdate.getRoles()) {
                        if (role.getName().equals(com.example.avtoforum.Model.Enum.ERole.ROLE_ADMIN)) { // ERole enum'ınızın tam yolunu kullanın
                            isAdminRolePresent = true;
                            break;
                        }
                    }
                }


                if (!isAdminRolePresent) {
                    if (adminToUpdate.getRoles() == null) {
                        adminToUpdate.setRoles(new HashSet<>());
                    }
                    adminToUpdate.getRoles().add(adminRoleEntity); // Eksikse admin rolünü ekle
                    userRepository.save(adminToUpdate);
                    System.out.println("DatabaseInitializer: ROLE_ADMIN added to existing user: '" + adminUsername + "'.");
                } else {
                    System.out.println("DatabaseInitializer: User '" + adminUsername + "' already exists and has ROLE_ADMIN.");
                }
                
                // Opsiyonel: Şifreyi her başlangıçta bu değere GÜNCELLEMEK İSTİYORSANIZ
                // Bu, şifreyi unutursanız kullanışlı olabilir ama dikkatli kullanılmalıdır.
                // Genellikle sadece kullanıcı yokken şifre atanır veya ayrı bir şifre sıfırlama mekanizması olur.
                /*
                if (passwordEncoder != null && !passwordEncoder.matches(adminPassword, adminToUpdate.getPassword())) {
                     adminToUpdate.setPassword(passwordEncoder.encode(adminPassword));
                     userRepository.save(adminToUpdate);
                     System.out.println("DatabaseInitializer: Password for admin user '" + adminUsername + "' has been reset/updated.");
                }
                */
            }
            System.out.println("DatabaseInitializer: Initialization process finished.");
        };
    }
}
