package com.example.avtoforum.Controller;

import com.example.avtoforum.Model.Dto.RequestDTO.TopicRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.TopicResponse;
import com.example.avtoforum.Security.UserDetailsImpl;
import com.example.avtoforum.Service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {


    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TopicResponse>> getTopicsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(topicService.getTopicsByCategory(categoryId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TopicResponse>> getTopicsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(topicService.getTopicsByUser(userId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TopicResponse>> getRecentTopics() {
        return ResponseEntity.ok(topicService.getRecentTopics());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicResponse> createTopic(
            @Valid @RequestBody TopicRequest topicRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(topicService.createTopic(topicRequest, userDetails.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequest topicRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        return ResponseEntity.ok(topicService.updateTopic(id, topicRequest, userDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        return ResponseEntity.ok(topicService.deleteTopic(id, userDetails.getUser()));
    }

    @PutMapping("/{id}/increment-views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        topicService.incrementViews(id);
        return ResponseEntity.ok().build();
    }
}
