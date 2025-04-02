package com.example.feedback_appointment.web;


import com.example.feedback_appointment.FeedbackAppointmentApplication;
import com.example.feedback_appointment.controller.FeedbackController;
import com.example.feedback_appointment.model.Feedback;
import com.example.feedback_appointment.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)

class FeedbackControllerApiTest {
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;


    private Feedback feedback;
    private UUID feedbackId;
    private UUID appointmentId;



    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(feedbackController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        feedbackId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        feedback = new Feedback();
        feedback.setId(feedbackId);
        feedback.setAppointmentId(appointmentId);
        feedback.setComment("Excellent service");
        feedback.setRating(5);
    }

    @Test
    void getAll_ShouldReturnAllFeedbacks() throws Exception {
        // Arrange
        List<Feedback> feedbacks = Arrays.asList(feedback, new Feedback());
        when(feedbackService.getAll()).thenReturn(feedbacks);

        // Act & Assert
        mockMvc.perform(get("/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(feedbackId.toString())));
    }

    @Test
    void create_ShouldReturnCreatedFeedback() throws Exception {
        // Arrange
        when(feedbackService.save(any(Feedback.class))).thenReturn(feedback);

        // Act & Assert
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(feedbackId.toString())))
                .andExpect(jsonPath("$.appointmentId", is(appointmentId.toString())))
                .andExpect(jsonPath("$.comment", is("Excellent service")))
                .andExpect(jsonPath("$.rating", is(5)));
    }

    @Test
    void getByAppointment_ShouldReturnFeedbacksForAppointment() throws Exception {
        // Arrange
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackService.getByAppointment(appointmentId)).thenReturn(feedbacks);

        // Act & Assert
        mockMvc.perform(get("/feedback/appointment/{id}", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].appointmentId", is(appointmentId.toString())));
    }

    @Test
    void getById_ShouldReturnFeedback() throws Exception {
        // Arrange
        when(feedbackService.getById(feedbackId)).thenReturn(feedback);

        // Act & Assert
        mockMvc.perform(get("/feedback/{id}", feedbackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(feedbackId.toString())))
                .andExpect(jsonPath("$.comment", is("Excellent service")));
    }
    @Test
    void getById_ShouldReturnNotFoundForInvalidId() throws Exception {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(feedbackService.getById(invalidId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found"));

        // Act & Assert
        mockMvc.perform(get("/feedback/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}