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

import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;

import com.juanperuzzo.flappynaruu.security.SignatureService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameSessionController.class)
@TestPropertySource(properties = "LEADERBOARD_SECRET=test-secret")
@Import(SignatureService.class)
public class GameSessionControllerTest {

    private static final String SECRET = "test-secret";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameSessionService service;

    @Autowired
    private ObjectMapper objectMapper;

    private String sign(String nickname, int score, long timestamp) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String payload = nickname + ":" + score + ":" + timestamp;
        byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : raw) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private SaveScoreRequest validRequest(String nickname, int score) throws Exception {
        long ts = Instant.now().toEpochMilli();
        return new SaveScoreRequest(nickname, score, ts, sign(nickname, score, ts));
    }

    @Test
    public void shouldReturn201WhenScoreIsHighscore() throws Exception {
        SaveScoreRequest request = validRequest("Uzzoper", 10);
        when(service.registerScoreIfHighscore("Uzzoper", 10)).thenReturn(true);

        mockMvc.perform(post("/leaderboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    public void shouldReturn204WhenScoreIsNotHighscore() throws Exception {
        SaveScoreRequest request = validRequest("Uzzoper", 1);
        when(service.registerScoreIfHighscore("Uzzoper", 1)).thenReturn(false);

        mockMvc.perform(post("/leaderboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturn400WhenNicknameHasBadWords() throws Exception {
        long ts = Instant.now().toEpochMilli();
        SaveScoreRequest request = new SaveScoreRequest("merda", 10, ts, sign("merda", 10, ts));

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

    @Test
    public void shouldReturn401WhenSignatureIsInvalid() throws Exception {
        long ts = Instant.now().toEpochMilli();
        SaveScoreRequest request = new SaveScoreRequest("Uzzoper", 10, ts, "deadbeef");

        mockMvc.perform(post("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn401WhenTimestampIsExpired() throws Exception {
        long ts = Instant.now().minus(Duration.ofMinutes(10)).toEpochMilli();
        SaveScoreRequest request = new SaveScoreRequest("Uzzoper", 10, ts, sign("Uzzoper", 10, ts));

        mockMvc.perform(post("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
