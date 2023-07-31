package com.joboffers.domain.loginandregister.dto;

import lombok.Builder;

@Builder
public record AccountDto(
        String username,
        String password) {
}
