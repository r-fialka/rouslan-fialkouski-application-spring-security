package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.receiver
            WHERE t.sender = :sender
            ORDER BY t.id DESC
            """)
    List<Transaction> findBySenderOrderByIdDesc(User sender);

}