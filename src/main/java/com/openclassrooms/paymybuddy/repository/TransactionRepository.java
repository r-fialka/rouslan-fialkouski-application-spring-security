package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for transaction persistence operations.
 */
public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    /**
     * Return all transactions sent by a user.
     * The receiver is loaded eagerly to avoid LazyInitializationException.
     *
     * @param sender the transaction sender
     * @return the list of transactions ordered by newest first
     */
    @Query("""
            SELECT t
            FROM Transaction t
            JOIN FETCH t.receiver
            WHERE t.sender = :sender
            ORDER BY t.id DESC
            """)
    List<Transaction> findBySenderOrderByIdDesc(User sender);

}