package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.AccountDto;

class AccountMapper {
    public static AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .username(account.username())
                .password(account.password())
                .build();
    }
}
