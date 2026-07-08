package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for user persistence operations.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by email address.
     *
     * @param email the user's email
     * @return the matching user, if found
     */
    Optional<User> findByEmail(String email);

}