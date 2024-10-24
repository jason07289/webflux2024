package com.example.webflux1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class UserUpdateRequest {
    String name;
    String email;
}
