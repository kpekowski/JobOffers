package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.RegisterUserDto;
import com.joboffers.domain.loginandregister.dto.RegistrationResultDto;
import com.joboffers.domain.loginandregister.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginAndRegisterFacadeTest {
    private final LoginRepository loginRepository = new LoginRepositoryTestImpl();

    @Test
    public void it_should_return_correct_response_when_successfully_register() {
        //given
        String username = "Kowalski";
        String password = "randompassword";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(loginRepository);
        //when
        RegistrationResultDto responseDto = loginAndRegisterFacade.register(new RegisterUserDto(username, password));
        //then
        assertThat(responseDto.created()).isTrue();
        assertThat(responseDto.username()).isEqualTo(username);
    }

    @Test
    public void it_should_find_user_by_username() {
        //given
        String username = "Kowalski";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(loginRepository);
        User alreadyExistingAccount = User.builder()
                .username(username)
                .password("12345678")
                .build();
        loginRepository.save(alreadyExistingAccount);
        //when
        UserDto accountDto = loginAndRegisterFacade.findByUsername(username);
        //then
        UserDto expectedAccountDto = UserDto.builder()
                .username(username)
                .password("12345678")
                .build();
        assertThat(accountDto).isEqualTo(expectedAccountDto);
    }

    @Test
    public void it_should_throw_an_exception_when_user_not_found() {
        //given
        String username = "Kowalski";
        LoginAndRegisterFacade loginAndRegisterFacade = new LoginAndRegisterFacade(loginRepository);
        //when
        //then
        assertThrows(BadCredentialsException.class, () -> loginAndRegisterFacade.findByUsername(username), "User not found");
    }
}