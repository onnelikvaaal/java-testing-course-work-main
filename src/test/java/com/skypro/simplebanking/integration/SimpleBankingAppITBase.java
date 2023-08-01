package com.skypro.simplebanking.integration;

import com.skypro.simplebanking.dto.BankingUserDetails;
import com.skypro.simplebanking.entity.Account;
import com.skypro.simplebanking.entity.AccountCurrency;
import com.skypro.simplebanking.entity.User;
import com.skypro.simplebanking.repository.AccountRepository;
import com.skypro.simplebanking.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleBankingAppITBase {

    protected static final String TEST_NAME = "Sasha";
    protected static final String TEST_PASSWORD = "11111";
    protected static final String TEST_NAME_2 = "Seryozha";
    protected static final String TEST_PASSWORD_2 = "22222";
    protected static final String TEST_NAME_3 = "Oleg";
    protected static final String TEST_PASSWORD_3 = "33333";
    protected static final AccountCurrency TEST_CURRENCY = AccountCurrency.RUB;
    protected static final Long TEST_AMOUNT = 1000L;
    protected static final Long TEST_DEPOSIT_AMOUNT = 100L;
    protected static final Long TEST_WITHDRAWAL_AMOUNT = 400L;
    protected static final Long TEST_TRANSFER_AMOUNT = 400L;

    protected Long savedUserId;
    protected Long savedUserId2;

    protected Long savedAccountId;
    protected Long savedAccountId2;
    protected Long savedAccountId3;
    protected Long savedAccountId4;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected UserRepository userRepository;

    protected final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    protected BankingUserDetails user;
    protected BankingUserDetails user2;
    protected BankingUserDetails admin;


    @BeforeAll
    public void before() {

        User userEntity = new User();
        userEntity.setUsername(TEST_NAME);
        userEntity.setPassword(TEST_PASSWORD);
        savedUserId = userRepository.save(userEntity).getId();

        User userEntity2 = new User();
        userEntity2.setUsername(TEST_NAME_2);
        userEntity2.setPassword(TEST_PASSWORD_2);
        savedUserId2 = userRepository.save(userEntity2).getId();

        Account account = new Account();
        account.setAccountCurrency(TEST_CURRENCY);
        account.setAmount(TEST_AMOUNT);
        account.setUser(userEntity);
        savedAccountId = accountRepository.save(account).getId();

        Account account2 = new Account();
        account2.setAccountCurrency(TEST_CURRENCY);
        account2.setAmount(TEST_AMOUNT);
        account2.setUser(userEntity);
        savedAccountId2 = accountRepository.save(account2).getId();

        Account account3 = new Account();
        account3.setAccountCurrency(TEST_CURRENCY);
        account3.setAmount(TEST_AMOUNT);
        account3.setUser(userEntity2);
        savedAccountId3 = accountRepository.save(account3).getId();

        Account account4 = new Account();
        account4.setAccountCurrency(TEST_CURRENCY);
        account4.setAmount(TEST_AMOUNT);
        account4.setUser(userEntity2);
        savedAccountId4 = accountRepository.save(account4).getId();


        user = new BankingUserDetails(savedUserId, TEST_NAME,
                passwordEncoder.encode(TEST_PASSWORD), false);
        user2 = new BankingUserDetails(savedUserId2, TEST_NAME_2,
                passwordEncoder.encode(TEST_PASSWORD_2), false);
        admin = new BankingUserDetails(-1, "Admin",
                passwordEncoder.encode("0000"), true);
    }

}
