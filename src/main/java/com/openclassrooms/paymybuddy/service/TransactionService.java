package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Provides transaction management operations.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;

    }

    /**
     * Return all transactions for the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(String email) {

        return transactionRepository.findBySenderOrderByIdDesc(
                findUserByEmail(email)
        );

    }

    /**
     * Create a new transaction.
     */
    @Transactional
    public void createTransaction(String senderEmail,
                                  Integer receiverId,
                                  String description,
                                  Double amount) {

        User sender = findUserByEmail(senderEmail);
        User receiver = findUserById(receiverId);

        // Prevent transfers to the same user.
        if (sender.getId().equals(receiver.getId())) {

            throw new IllegalArgumentException(
                    "You cannot send money to yourself."
            );

        }

        // Validate transfer amount.
        if (amount == null || amount <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );

        }

        Transaction transaction = new Transaction();

        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setAmount(amount);

        transactionRepository.save(transaction);

    }

    /**
     * Find a user by email.
     */
    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

    }

    /**
     * Find a user by id.
     */
    private User findUserById(Integer id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

    }

}