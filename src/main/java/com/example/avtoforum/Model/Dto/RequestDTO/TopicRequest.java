package com.example.avtoforum.Model.Dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(min = 5, max = 100, message = "Başlık 5-100 karakter arasında olmalıdır")
    private String title;

    @NotBlank(message = "İçerik boş olamaz")
    private String content;

    @NotNull(message = "Kategori ID boş olamaz")
    private Long categoryId;
}
