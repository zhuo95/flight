package com.zz.flight.repository;

import com.zz.flight.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    Page<User> findAllByRole(Integer role, Pageable pageable);

    List<User> findAllByRole(int role);

}
