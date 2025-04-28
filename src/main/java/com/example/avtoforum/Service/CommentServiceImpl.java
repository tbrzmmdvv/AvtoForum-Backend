package com.example.avtoforum.Service;

import com.example.avtoforum.Exception.ResourceNotFoundException;
import com.example.avtoforum.Model.Dto.RequestDTO.CommentRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.CommentResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.Comment;
import com.example.avtoforum.Model.Entity.Role;
import com.example.avtoforum.Model.Entity.Topic;
import com.example.avtoforum.Model.Entity.User;
import com.example.avtoforum.Repository.CommentRepository;
import com.example.avtoforum.Repository.TopicRepository;
import com.example.avtoforum.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;


    private final TopicRepository topicRepository;


    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, TopicRepository topicRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentResponse> getCommentsByTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        List<Comment> comments = commentRepository.findByTopic(topic);

        return comments.stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Comment> comments = commentRepository.findByUser(user);

        return comments.stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse createComment(CommentRequest commentRequest, User currentUser) {
        Topic topic = topicRepository.findById(commentRequest.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", commentRequest.getTopicId()));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTopic(topic);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);

        return mapCommentToResponse(savedComment);
    }

    @Override
    public CommentResponse updateComment(Long id, CommentRequest commentRequest, User currentUser) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        // Yalnızca yorumu oluşturan kullanıcı veya admin/mod güncelleyebilir
        if (!comment.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(r ->
                        r.getName() == Role.ERole.ROLE_ADMIN || r.getName() == Role.ERole.ROLE_MODERATOR)) {
            throw new AccessDeniedException("Bu yorumu güncelleme yetkiniz bulunmamaktadır!");
        }

        // Yorum içeriğini güncelle, konu değiştirilmez
        comment.setContent(commentRequest.getContent());

        Comment updatedComment = commentRepository.save(comment);

        return mapCommentToResponse(updatedComment);
    }

    @Override
    public MessageResponse deleteComment(Long id, User currentUser) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        // Yalnızca yorumu oluşturan kullanıcı veya admin/mod silebilir
        if (!comment.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(r ->
                        r.getName() == Role.ERole.ROLE_ADMIN || r.getName() == Role.ERole.ROLE_MODERATOR)) {
            throw new AccessDeniedException("Bu yorumu silme yetkiniz bulunmamaktadır!");
        }

        commentRepository.delete(comment);

        return new MessageResponse("Yorum başarıyla silindi!");
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getTopic().getId(),
                comment.getCreatedAt()
        );
    }
}
