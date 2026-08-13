package com.juanperuzzo.flappynaruu.controller;

import com.juanperuzzo.flappynaruu.security.RateLimitFilter;
import com.juanperuzzo.flappynaruu.security.SignatureService;
import com.juanperuzzo.flappynaruu.service.GameSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameSessionController.class)
@TestPropertySource(properties = {"RATE_LIMIT_MAX_REQUESTS=1", "RATE_LIMIT_WINDOW_SECONDS=60", "LEADERBOARD_SECRET=test-secret"})
@Import({RateLimitFilter.class, SignatureService.class})
public class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameSessionService service;

    @Test
    public void shouldReturn429AfterLimitExceeded() throws Exception {
        // First request passes the filter (fails validation with 400, proving it reached the controller)
        mockMvc.perform(post("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        // Second request within the window is blocked by the filter
        mockMvc.perform(post("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void shouldNotLimitGetRequests() throws Exception {
        when(service.getTop5()).thenReturn(List.of());

        mockMvc.perform(get("/leaderboard/top5"))
                .andExpect(status().isOk());
    }
}