package com.example.feedback_appointment;


import com.example.feedback_appointment.model.Feedback;
import com.example.feedback_appointment.repository.FeedbackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FeedbackAppointmentApplication.class)
@AutoConfigureMockMvc
@Transactional
public class FeedbackITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private ObjectMapper objectMapper;
    private UUID testAppointmentId;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testAppointmentId = UUID.randomUUID();

        testFeedback = new Feedback();
        testFeedback.setAppointmentId(testAppointmentId);
        testFeedback.setRating(5);
        testFeedback.setComment("Excellent service");
        testFeedback = feedbackRepository.save(testFeedback);
    }

    @Test
    void createFeedback_ShouldReturnCreatedFeedback() throws Exception {
        Feedback newFeedback = new Feedback();
        newFeedback.setAppointmentId(testAppointmentId);
        newFeedback.setRating(4);
        newFeedback.setComment("Good service");

        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFeedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.appointmentId", is(testAppointmentId.toString())))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.comment", is("Good service")));

        List<Feedback> feedbacks = feedbackRepository.findByAppointmentId(testAppointmentId);
        assertEquals(2, feedbacks.size());
    }

    @Test
    void getAllFeedbacks_ShouldReturnAllFeedbacks() throws Exception {
        // Добавяне само на очакваните feedbacks
        Feedback anotherFeedback = new Feedback();
        anotherFeedback.setAppointmentId(testAppointmentId);
        anotherFeedback.setRating(3);
        anotherFeedback.setComment("Average service");
        feedbackRepository.save(anotherFeedback);

        mockMvc.perform(get("/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].appointmentId",
                        hasItem(testAppointmentId.toString())));
    }

    @Test
    void getFeedbackById_ShouldReturnFeedback() throws Exception {
        mockMvc.perform(get("/feedback/{id}", testFeedback.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testFeedback.getId().toString())))
                .andExpect(jsonPath("$.comment", is("Excellent service")));
    }

    @Test
    void getFeedbackById_ShouldReturnNotFoundForInvalidId() throws Exception {
        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(get("/feedback/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFeedbacksByAppointment_ShouldReturnCorrectFeedbacks() throws Exception {
        // Create feedback for different appointment
        UUID otherAppointmentId = UUID.randomUUID();
        Feedback otherFeedback = new Feedback();
        otherFeedback.setAppointmentId(otherAppointmentId);
        otherFeedback.setRating(2);
        otherFeedback.setComment("Poor service");
        feedbackRepository.save(otherFeedback);

        mockMvc.perform(get("/feedback/appointment/{id}", testAppointmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].appointmentId", is(testAppointmentId.toString())))
                .andExpect(jsonPath("$[0].comment", is("Excellent service")));
    }

    @Test
    void createFeedback_ShouldSetCreatedAtAutomatically() throws Exception {
        LocalDateTime beforeTest = LocalDateTime.now();

        Feedback newFeedback = new Feedback();
        newFeedback.setAppointmentId(testAppointmentId);
        newFeedback.setRating(4);
        newFeedback.setComment("Good service");

        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFeedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        List<Feedback> feedbacks = feedbackRepository.findByAppointmentId(testAppointmentId);
        Feedback savedFeedback = feedbacks.get(1);
        assert(savedFeedback.getCreatedAt().isAfter(beforeTest) ||
                savedFeedback.getCreatedAt().equals(beforeTest));
    }
}