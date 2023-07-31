package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.AccountDto;
import com.joboffers.domain.loginandregister.dto.LoginAndRegisterResponseDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginAndRegisterFacade {
    private final AccountRepository accountRepository;

    public LoginAndRegisterResponseDto register(String username, String password) {
        if (accountRepository.existsByUsername(username)) {
            return new LoginAndRegisterResponseDto(null, "User already exists");
        }
        Account generatedAccount = Account.builder()
                .username(username)
                .password(password)
                .build();
        accountRepository.save(generatedAccount);
        return new LoginAndRegisterResponseDto(AccountMapper.mapToDto(generatedAccount), "User registered successfully");
    }

    public AccountDto findByUsername(String username) {
        Account accountByUsername = accountRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return AccountMapper.mapToDto(accountByUsername);
    }
}
