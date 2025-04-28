package com.example.avtoforum.Repository;

import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Model.Enumeration.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(Role.ERole name);
}
