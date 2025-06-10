package com.nova.users.repository;


import com.nova.users.model.Role;
import com.nova.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findByRol(Role rol, Pageable pageable);
}

