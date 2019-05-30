package com.brainsci.springsecurity.repository;

import com.brainsci.springsecurity.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    @Query(value = "SELECT ac FROM UserRole ac WHERE ac.user.id = ?1 AND valid = 1")
    Page<UserRole> findUserBasesByAdminId(Integer userId, Pageable pageable);

    @Query(value = "SELECT ac FROM UserRole ac WHERE ac.user.id = ?1 AND valid = 1")
    List<UserRole> findUserBaseListByUserId(Integer userId);

    @Query(value = "SELECT ac FROM UserRole ac WHERE ac.user.email = ?1 AND valid = 1")
    List<UserRole> findUserBaseListByUserEmail(String email);

    @Query(value = "SELECT ac FROM UserRole ac WHERE ac.role.id = ?1 AND valid = 1")
    Page<UserRole> findUserBasesByRoleId(Integer roleId, Pageable pageable);

    @Query(value = "SELECT ac FROM UserRole ac WHERE ac.id = ?1 AND valid = 1")
    UserRole findUserBaseById(Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE UserRole ac SET ac.valid = 0 WHERE ac.id = ?1 AND ac.valid = 1")
    int deleteUserBaseById(Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE UserRole ac SET ac.valid = 0 WHERE ac.user.id = ?1 AND ac.valid = 1")
    int deleteUserBaseByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE UserRole ac SET ac.valid = 0 WHERE ac.role.id = ?1 AND ac.valid = 1")
    int deleteUserBaseByRoleId(Integer roleId);
}
