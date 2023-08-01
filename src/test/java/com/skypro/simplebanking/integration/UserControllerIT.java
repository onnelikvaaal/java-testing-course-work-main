package com.skypro.simplebanking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.simplebanking.dto.CreateUserRequest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerIT extends SimpleBankingAppITBase {

    @Test
    void createUser_creates_user_with_admin_access() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername(TEST_NAME_3);
        userRequest.setPassword(TEST_PASSWORD_3);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/user").with(user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/list").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].username").value(TEST_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].username").value(TEST_NAME_2))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].username").value(TEST_NAME_3));
    }

    @Test
    void getAllUsers_returns_correct_list_of_users() throws Exception {
        mockMvc.perform(get("/user/list").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].username").value(TEST_NAME))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].username").value(TEST_NAME_2))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].username").value(TEST_NAME_3));
    }

    @Test
    void getAllUsers_returns_403_for_admin() throws Exception {
        mockMvc.perform(get("/user/list").with(user(admin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyProfile_returns_my_profile() throws Exception {
        mockMvc.perform(get("/user/me").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(TEST_NAME));
    }

    @Test
    void getMyProfile_returns_403_for_admin() throws Exception {
        mockMvc.perform(get("/user/me").with(user(admin)))
                .andExpect(status().isForbidden());
    }
}
