package com.example.feedback_appointment.service;



import com.example.feedback_appointment.model.Feedback;
import com.example.feedback_appointment.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceUTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback feedback;
    private UUID feedbackId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        feedbackId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        feedback = new Feedback();
        feedback.setId(feedbackId);
        feedback.setAppointmentId(appointmentId);
        feedback.setComment("Test comment");
        feedback.setRating(5);
    }

    @Test
    void save_ShouldReturnSavedFeedback() {
        // Arrange
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        // Act
        Feedback savedFeedback = feedbackService.save(feedback);

        // Assert
        assertNotNull(savedFeedback);
        assertEquals(feedbackId, savedFeedback.getId());
        verify(feedbackRepository, times(1)).save(feedback);
    }

    @Test
    void getAll_ShouldReturnAllFeedbacks() {
        // Arrange
        List<Feedback> feedbacks = Arrays.asList(feedback, new Feedback());
        when(feedbackRepository.findAll()).thenReturn(feedbacks);

        // Act
        List<Feedback> result = feedbackService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void getByAppointment_ShouldReturnFeedbacksForAppointment() {
        // Arrange
        List<Feedback> feedbacks = Arrays.asList(feedback);
        when(feedbackRepository.findByAppointmentId(appointmentId)).thenReturn(feedbacks);

        // Act
        List<Feedback> result = feedbackService.getByAppointment(appointmentId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(appointmentId, result.get(0).getAppointmentId());
        verify(feedbackRepository, times(1)).findByAppointmentId(appointmentId);
    }

    @Test
    void getById_ShouldReturnFeedbackWhenExists() {
        // Arrange
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

        // Act
        Feedback result = feedbackService.getById(feedbackId);

        // Assert
        assertNotNull(result);
        assertEquals(feedbackId, result.getId());
        verify(feedbackRepository, times(1)).findById(feedbackId);
    }

    @Test
    void getById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(feedbackRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> feedbackService.getById(nonExistingId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Feedback not found", exception.getReason());
        verify(feedbackRepository, times(1)).findById(nonExistingId);
    }
}