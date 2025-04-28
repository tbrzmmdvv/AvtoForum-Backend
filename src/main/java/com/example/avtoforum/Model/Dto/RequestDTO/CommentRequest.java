package com.example.avtoforum.Model.Dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Yorum içeriği boş olamaz")
    private String content;

    @NotNull(message = "Konu ID boş olamaz")
    private Long topicId;
}
