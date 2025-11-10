package com.virtual.assistance.UserManagement.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virtual.assistance.UserManagement.models.UserInfo;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, UUID> {

    Optional<UserInfo> findByEmail(String email);
}
