package com.skypro.simplebanking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.simplebanking.dto.BalanceChangeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class AccountControllerIT extends SimpleBankingAppITBase {

    @Test
    void getUserAccount_returns_correct_account() throws Exception {
        mockMvc.perform(get("/account/" + savedAccountId).with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccountId))
                .andExpect(jsonPath("$.amount").value(TEST_AMOUNT))
                .andExpect(jsonPath("$.currency").value(TEST_CURRENCY.name()));
    }

    @Test
    void depositToAccount_deposits_to_account() throws Exception {
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest();
        balanceChangeRequest.setAmount(TEST_DEPOSIT_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(balanceChangeRequest);

        mockMvc.perform(post("/account/deposit/" + savedAccountId)
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccountId))
                .andExpect(jsonPath("$.amount").value(TEST_AMOUNT + TEST_DEPOSIT_AMOUNT))
                .andExpect(jsonPath("$.currency").value(TEST_CURRENCY.name()));
    }

    @Test
    void depositToAccount_returns_404_when_user_deposits_to_not_own_account() throws Exception {
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest();
        balanceChangeRequest.setAmount(TEST_WITHDRAWAL_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(balanceChangeRequest);

        mockMvc.perform(post("/account/deposit/" + savedAccountId3)
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void withdrawFromAccount_withdraws_from_account() throws Exception {
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest();
        balanceChangeRequest.setAmount(TEST_WITHDRAWAL_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(balanceChangeRequest);

        mockMvc.perform(post("/account/withdraw/" + savedAccountId2)
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccountId2))
                .andExpect(jsonPath("$.amount").value(TEST_AMOUNT - TEST_WITHDRAWAL_AMOUNT))
                .andExpect(jsonPath("$.currency").value(TEST_CURRENCY.name()));
    }

    @Test
    void withdrawFromAccount_returns_404_when_user_withdraws_from_not_own_account() throws Exception {
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest();
        balanceChangeRequest.setAmount(TEST_WITHDRAWAL_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(balanceChangeRequest);

        mockMvc.perform(post("/account/withdraw/" + savedAccountId3)
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }


    @Test
    void getUserAccount_returns_403_for_admin() throws Exception {
        mockMvc.perform(get("/account/" + savedAccountId).with(user(admin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void depositToAccount_returns_403_for_admin() throws Exception {
        mockMvc.perform(post("/account/deposit/" + savedAccountId).with(user(admin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void withdrawFromAccount_returns_403_for_admin() throws Exception {
        mockMvc.perform(post("/account/withdraw/" + savedAccountId2).with(user(admin)))
                .andExpect(status().isForbidden());
    }
}
