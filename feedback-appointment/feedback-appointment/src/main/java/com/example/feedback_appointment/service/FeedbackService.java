package com.example.feedback_appointment.service;

import com.example.feedback_appointment.model.Feedback;
import com.example.feedback_appointment.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> getByAppointment(UUID appointmentId) {
        return feedbackRepository.findByAppointmentId(appointmentId);
    }
}