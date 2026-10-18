package com.gravadoracampista.dtos.response;

import lombok.Builder;

@Builder
public record TokenResponseDto(String token, String refreshToken) {
}