package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {

        sender = new User();
        sender.setId(1);
        sender.setUserName("Sender");
        sender.setEmail("sender@test.com");

        receiver = new User();
        receiver.setId(2);
        receiver.setUserName("Receiver");
        receiver.setEmail("receiver@test.com");

    }

    /**
     * Verify that all transactions are returned
     * for the authenticated user.
     */
    @Test
    void shouldReturnTransactions() {

        // Arrange

        Transaction transaction = new Transaction();

        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription("Dinner");
        transaction.setAmount(25.0);

        List<Transaction> transactions = List.of(transaction);

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(transactionRepository.findBySenderOrderByIdDesc(sender))
                .thenReturn(transactions);

        // Act

        List<Transaction> result =
                transactionService.getTransactions(sender.getEmail());

        // Assert

        assertThat(result)
                .hasSize(1)
                .containsExactly(transaction);

        verify(userRepository)
                .findByEmail(sender.getEmail());

        verify(transactionRepository)
                .findBySenderOrderByIdDesc(sender);

    }

    /**
     * Verify that getting transactions fails
     * when the user does not exist.
     */
    @Test
    void shouldThrowWhenGettingTransactionsForUnknownUser() {

        // Arrange

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.getTransactions(
                                "unknown@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(userRepository)
                .findByEmail("unknown@test.com");

        verifyNoInteractions(transactionRepository);

    }

    /**
     * Verify that a transaction is created successfully.
     */
    @Test
    void shouldCreateTransactionSuccessfully() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));

        // Act

        transactionService.createTransaction(
                sender.getEmail(),
                receiver.getId(),
                "Dinner",
                25.0
        );

        // Assert

        verify(transactionRepository)
                .save(any(Transaction.class));

    }

    /**
     * Verify that creating a transaction fails
     * when the sender does not exist.
     */
    @Test
    void shouldThrowWhenSenderNotFound() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                receiver.getId(),
                                "Dinner",
                                25.0)
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(transactionRepository, never())
                .save(any());

    }

    /**
     * Verify that creating a transaction fails
     * when the receiver does not exist.
     */
    @Test
    void shouldThrowWhenReceiverNotFound() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                receiver.getId(),
                                "Dinner",
                                25.0)
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(transactionRepository, never())
                .save(any());

    }

    /**
     * Verify that a user cannot send money to themselves.
     */
    @Test
    void shouldThrowWhenSendingMoneyToYourself() {

        // Arrange

        receiver.setId(sender.getId());

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(sender.getId()))
                .thenReturn(Optional.of(receiver));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                sender.getId(),
                                "Dinner",
                                25.0)
                );

        assertThat(exception.getMessage())
                .isEqualTo("You cannot send money to yourself.");

        verify(transactionRepository, never())
                .save(any());

    }

    /**
     * Verify that a null amount is rejected.
     */
    @Test
    void shouldThrowWhenAmountIsNull() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                receiver.getId(),
                                "Dinner",
                                null)
                );

        assertThat(exception.getMessage())
                .isEqualTo("Amount must be greater than zero.");

        verify(transactionRepository, never())
                .save(any());

    }

    /**
     * Verify that a zero amount is rejected.
     */
    @Test
    void shouldThrowWhenAmountIsZero() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                receiver.getId(),
                                "Dinner",
                                0.0)
                );

        assertThat(exception.getMessage())
                .isEqualTo("Amount must be greater than zero.");

        verify(transactionRepository, never())
                .save(any());

    }

    /**
     * Verify that a negative amount is rejected.
     */
    @Test
    void shouldThrowWhenAmountIsNegative() {

        // Arrange

        when(userRepository.findByEmail(sender.getEmail()))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(
                                sender.getEmail(),
                                receiver.getId(),
                                "Dinner",
                                -10.0)
                );

        assertThat(exception.getMessage())
                .isEqualTo("Amount must be greater than zero.");

        verify(transactionRepository, never())
                .save(any());

    }

}