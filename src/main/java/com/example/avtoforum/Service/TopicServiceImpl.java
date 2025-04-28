package com.example.avtoforum.Service;

import com.example.avtoforum.Exception.ResourceNotFoundException;
import com.example.avtoforum.Model.Dto.RequestDTO.TopicRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.CommentResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.TopicResponse;
import com.example.avtoforum.Model.Entity.Category;
import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Model.Entity.Topic;
import com.example.avtoforum.Model.Entity.User;
import com.example.avtoforum.Repository.CategoryRepository;
import com.example.avtoforum.Repository.TopicRepository;
import com.example.avtoforum.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {


    private final TopicRepository topicRepository;


    private final CategoryRepository categoryRepository;


    private final UserRepository userRepository;

    public TopicServiceImpl(TopicRepository topicRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TopicResponse> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(this::mapTopicToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponse getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        return mapTopicToResponse(topic);
    }

    @Override
    public List<TopicResponse> getTopicsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Topic> topics = topicRepository.findByCategoryOrderByCreatedAtDesc(category);

        return topics.stream()
                .map(this::mapTopicToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopicResponse> getTopicsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Topic> topics = topicRepository.findByUser(user);

        return topics.stream()
                .map(this::mapTopicToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopicResponse> getRecentTopics() {
        List<Topic> recentTopics = topicRepository.findTop5ByOrderByCreatedAtDesc();

        return recentTopics.stream()
                .map(this::mapTopicToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponse createTopic(TopicRequest topicRequest, User currentUser) {
        Category category = categoryRepository.findById(topicRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", topicRequest.getCategoryId()));

        Topic topic = new Topic();
        topic.setTitle(topicRequest.getTitle());
        topic.setContent(topicRequest.getContent());
        topic.setCategory(category);
        topic.setUser(currentUser);
        topic.setViews(0);

        Topic savedTopic = topicRepository.save(topic);

        return mapTopicToResponse(savedTopic);
    }

    @Override
    public TopicResponse updateTopic(Long id, TopicRequest topicRequest, User currentUser) throws AccessDeniedException {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        // Yalnızca konuyu oluşturan kullanıcı veya admin/mod güncelleyebilir
        if (!topic.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(r ->
                        r.getName() == Role.ERole.ROLE_ADMIN || r.getName() == Role.ERole.ROLE_MODERATOR)) {
            throw new AccessDeniedException("Bu konuyu güncelleme yetkiniz bulunmamaktadır!");
        }

        Category category = categoryRepository.findById(topicRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", topicRequest.getCategoryId()));

        topic.setTitle(topicRequest.getTitle());
        topic.setContent(topicRequest.getContent());
        topic.setCategory(category);

        Topic updatedTopic = topicRepository.save(topic);

        return mapTopicToResponse(updatedTopic);
    }

    @Override
    public MessageResponse deleteTopic(Long id, User currentUser) throws AccessDeniedException {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        // Yalnızca konuyu oluşturan kullanıcı veya admin/mod silebilir
        if (!topic.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(r ->
                        r.getName() == Role.ERole.ROLE_ADMIN || r.getName() == Role.ERole.ROLE_MODERATOR)) {
            throw new AccessDeniedException("Bu konuyu silme yetkiniz bulunmamaktadır!");
        }

        topicRepository.delete(topic);

        return new MessageResponse("Konu başarıyla silindi!");
    }

    @Override
    public void incrementViews(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        topic.setViews(topic.getViews() + 1);
        topicRepository.save(topic);
    }

    private TopicResponse mapTopicToResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setId(topic.getId());
        response.setTitle(topic.getTitle());
        response.setContent(topic.getContent());
        response.setUsername(topic.getUser().getUsername());
        response.setCategoryId(topic.getCategory().getId());
        response.setCategoryName(topic.getCategory().getName());
        response.setViews(topic.getViews());
        response.setCommentCount(topic.getComments().size());
        response.setCreatedAt(topic.getCreatedAt());
        response.setUpdatedAt(topic.getUpdatedAt());

        if (topic.getComments() != null && !topic.getComments().isEmpty()) {
            List<CommentResponse> comments = topic.getComments().stream()
                    .map(comment -> new CommentResponse(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser().getUsername(),
                            comment.getTopic().getId(),
                            comment.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            response.setComments(comments);
        }

        return response;
    }
}
