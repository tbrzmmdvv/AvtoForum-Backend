package com.example.avtoforum.Model.Dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Kategori adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kategori adı 3-50 karakter arasında olmalıdır")
    private String name;

    private String description;
}