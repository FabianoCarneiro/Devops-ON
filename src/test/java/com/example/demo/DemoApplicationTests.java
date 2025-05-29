package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assert(true);
    }

    @Test
    void healthCheckEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("HEALTH CHECK OK!"));
    }

    @Test
    void publicEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is public endpoint"));
    }

    @Test
    void devopsEndpointShouldReturnZumbi() throws Exception {
        mockMvc.perform(get("/devops"))
                .andExpect(status().isOk())
                .andExpect(content().string("Zumbi"));
    }

    @Test
    void autoglassEndpointShouldReturnUrl() throws Exception {
        mockMvc.perform(get("/autoglass"))
                .andExpect(status().isOk())
                .andExpect(content().string("https://www.autoglassonline.com.br/"));
    }

    @Test
    void whatIsTheTimeEndpointShouldReturnDate() throws Exception {
        mockMvc.perform(get("/what-is-the-time"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void securedEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/secured"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedAdminEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/secured-admin"))
                .andExpect(status().isUnauthorized());
    }
}
