package com.example.feedback_appointment.controller;

import com.example.feedback_appointment.model.Feedback;
import com.example.feedback_appointment.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public List<Feedback> getAll() {
        return feedbackService.getAll();
    }

    @PostMapping
    public Feedback create(@RequestBody Feedback feedback) {
        return feedbackService.save(feedback);
    }

    // Допълнително за retake:
    @GetMapping("/appointment/{id}")
    public List<Feedback> getByAppointment(@PathVariable UUID id) {
        return feedbackService.getByAppointment(id);
    }
}