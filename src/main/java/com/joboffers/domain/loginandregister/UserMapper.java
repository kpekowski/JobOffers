package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.UserDto;

class UserMapper {
    public static UserDto mapToDto(User account) {
        return UserDto.builder()
                .username(account.username())
                .password(account.password())
                .build();
    }
}
