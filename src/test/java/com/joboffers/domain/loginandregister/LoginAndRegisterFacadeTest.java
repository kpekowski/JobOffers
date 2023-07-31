package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.AccountDto;
import com.joboffers.domain.loginandregister.dto.LoginAndRegisterResponseDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginAndRegisterFacadeTest {
    private final AccountRepository accountRepository = new AccountRepositoryTestImpl();

    @Test
    public void it_should_return_failed_message_when_registering_if_user_already_exist() {
        //given
        String username = "Kowalski";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(accountRepository);
        Account alreadyExistingAccount = Account.builder()
                .username(username)
                .password("12345678")
                .build();
        accountRepository.save(alreadyExistingAccount);
        //when
        LoginAndRegisterResponseDto responseDto = loginAndRegisterFacade.register(username, "randompassword");
        //then
        assertThat(responseDto.message()).isEqualTo("User already exists");
    }

    @Test
    public void it_should_return_correct_response_when_successfully_register() {
        //given
        String username = "Kowalski";
        String password = "randompassword";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(accountRepository);
        //when
        LoginAndRegisterResponseDto responseDto = loginAndRegisterFacade.register(username, password);
        //then
        LoginAndRegisterResponseDto expectedResponse = new LoginAndRegisterResponseDto(AccountDto.builder()
                .username(username)
                .password(password)
                .build(), "User registered successfully");
        assertThat(responseDto).isEqualTo(expectedResponse);
    }

    @Test
    public void it_should_find_user_by_username() {
        //given
        String username = "Kowalski";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(accountRepository);
        Account alreadyExistingAccount = Account.builder()
                .username(username)
                .password("12345678")
                .build();
        accountRepository.save(alreadyExistingAccount);
        //when
        AccountDto accountDto = loginAndRegisterFacade.findByUsername(username);
        //then
        AccountDto expectedAccountDto = AccountDto.builder()
                .username(username)
                .password("12345678")
                .build();
        assertThat(accountDto).isEqualTo(expectedAccountDto);
    }

    @Test
    public void it_should_throw_an_exception_when_user_not_found() {
        //given
        String username = "Kowalski";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(accountRepository);
        //when
        //then
        assertThrows(UserNotFoundException.class, () -> loginAndRegisterFacade.findByUsername(username), "User not found");
    }
}