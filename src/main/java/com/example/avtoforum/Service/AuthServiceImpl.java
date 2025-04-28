package com.example.avtoforum.Service;

import com.example.avtoforum.Model.Dto.RequestDTO.LoginRequest;
import com.example.avtoforum.Model.Dto.RequestDTO.RegisterRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.JwtResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Model.Entity.User;
import com.example.avtoforum.Repository.RoleRepository;
import com.example.avtoforum.Repository.UserRepository;
import com.example.avtoforum.Security.JwtTokenProvider;
import com.example.avtoforum.Security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok ile final fieldlar için constructor oluşturur
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Kullanıcı doğrulama işlemi
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Güvenlik bağlamını ayarla
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT token oluştur
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Kullanıcı detaylarını al
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // JWT yanıtını döndür
        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                userDetails.getProfilePicture()
        );
    }

    @Override
    @Transactional
    public MessageResponse registerUser(RegisterRequest registerRequest) {
        // Kullanıcı adı kontrolü
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new MessageResponse("Hata: Kullanıcı adı zaten alınmış!");
        }

        // E-posta kontrolü
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new MessageResponse("Hata: E-posta adresi zaten kullanılıyor!");
        }

        // Yeni kullanıcı oluştur
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword())); // Şifreyi hashle

        // Rolleri ayarla
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Varsayılan rol: USER
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Hata: Rol bulunamadı."));
            roles.add(userRole);
        } else {
            // İstenen rolleri ayarla
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Hata: Rol bulunamadı."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(Role.ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Hata: Rol bulunamadı."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Hata: Rol bulunamadı."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user); // Kullanıcıyı veritabanına kaydet

        return new MessageResponse("Kullanıcı kaydı başarıyla tamamlandı!");
    }
}