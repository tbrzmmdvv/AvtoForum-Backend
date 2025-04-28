package com.example.avtoforum.Service;


import com.example.avtoforum.Model.Dto.RequestDTO.TopicRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.TopicResponse;
import com.example.avtoforum.Model.Entity.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TopicService {

    List<TopicResponse> getAllTopics();

    TopicResponse getTopicById(Long id);

    List<TopicResponse> getTopicsByCategory(Long categoryId);

    List<TopicResponse> getTopicsByUser(Long userId);

    List<TopicResponse> getRecentTopics();

    TopicResponse createTopic(TopicRequest topicRequest, User currentUser);

    TopicResponse updateTopic(Long id, TopicRequest topicRequest, User currentUser) throws AccessDeniedException;

    MessageResponse deleteTopic(Long id, User currentUser) throws AccessDeniedException;

    void incrementViews(Long id);
}
