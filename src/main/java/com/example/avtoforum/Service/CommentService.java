package com.example.avtoforum.Service;


import com.example.avtoforum.Model.Dto.RequestDTO.CommentRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.CommentResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CommentService {

    List<CommentResponse> getCommentsByTopic(Long topicId);

    List<CommentResponse> getCommentsByUser(Long userId);

    CommentResponse createComment(CommentRequest commentRequest, User currentUser);

    CommentResponse updateComment(Long id, CommentRequest commentRequest, User currentUser) throws AccessDeniedException;

    MessageResponse deleteComment(Long id, User currentUser) throws AccessDeniedException;
}
