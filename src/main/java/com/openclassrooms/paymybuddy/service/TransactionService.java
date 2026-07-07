package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getTransactions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        return transactionRepository.findBySenderOrderByIdDesc(user);
    }

    @Transactional
    public void saveTransaction(String senderEmail,
                                Integer receiverId,
                                String description,
                                Double amount) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sender not found."));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Receiver not found."));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You cannot send money to yourself.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Transaction transaction = new Transaction();

        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setAmount(amount);

        transactionRepository.save(transaction);
    }

}