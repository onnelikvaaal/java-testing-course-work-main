package com.skypro.simplebanking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.simplebanking.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TransferControllerIT extends SimpleBankingAppITBase {

    @Test
    void transfer_successfully_transfers() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(savedAccountId);
        transferRequest.setToUserId(savedUserId2);
        transferRequest.setToAccountId(savedAccountId3);
        transferRequest.setAmount(TEST_TRANSFER_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transferRequest);

        mockMvc.perform(post("/transfer")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/account/" + savedAccountId).with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccountId))
                .andExpect(jsonPath("$.amount").value(TEST_AMOUNT - TEST_TRANSFER_AMOUNT))
                .andExpect(jsonPath("$.currency").value(TEST_CURRENCY.name()));

        mockMvc.perform(get("/account/" + savedAccountId3).with(user(user2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccountId3))
                .andExpect(jsonPath("$.amount").value(TEST_AMOUNT + TEST_TRANSFER_AMOUNT))
                .andExpect(jsonPath("$.currency").value(TEST_CURRENCY.name()));
    }

    @Test
    void transfer_returns_403_for_admin() throws Exception {
        mockMvc.perform(post("/transfer").with(user(admin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void transfer_returns_404_when_user_transfers_from_not_own_account() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(savedAccountId3);
        transferRequest.setToUserId(savedUserId);
        transferRequest.setToAccountId(savedAccountId);
        transferRequest.setAmount(TEST_TRANSFER_AMOUNT);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transferRequest);

        mockMvc.perform(post("/transfer")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}
