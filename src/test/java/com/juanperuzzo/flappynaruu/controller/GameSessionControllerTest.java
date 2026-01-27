package com.juanperuzzo.flappynaruu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanperuzzo.flappynaruu.controller.request.SaveScoreRequest;
import com.juanperuzzo.flappynaruu.service.GameSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameSessionController.class)
public class GameSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameSessionService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturn201WhenScoreIsHighscore() throws Exception {
        SaveScoreRequest request = new SaveScoreRequest("Uzzoper", 10);

        when(service.registerScoreIfHighscore("Uzzoper", 10)).thenReturn(true);

        mockMvc.perform(post("/leaderboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    public void shouldReturn204WhenScoreIsNotHighscore() throws Exception {
        SaveScoreRequest request = new SaveScoreRequest("Uzzoper", 1);

        when(service.registerScoreIfHighscore("Uzzoper", 1)).thenReturn(false);

        mockMvc.perform(post("/leaderboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturn400WhenNicknameHasBadWords() throws Exception {
        // "merda" is in bad-words.txt
        SaveScoreRequest request = new SaveScoreRequest("merda", 10);

        mockMvc.perform(post("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    if (!response.contains("Nickname não pode conter palavras inapropriadas")) {
                        throw new AssertionError("Response does not contain expected message: " + response);
                    }
                });
    }
}