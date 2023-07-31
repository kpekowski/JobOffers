package com.joboffers.domain.loginandregister.dto;

public record LoginAndRegisterResponseDto(
        AccountDto accountDto,
        String message) {
}
