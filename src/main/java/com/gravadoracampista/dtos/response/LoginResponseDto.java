package com.gravadoracampista.dtos.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(String token, String refresh, String primeiroNome, Boolean isAdmin) {
}