package com.joboffers.domain.loginandregister;

import com.joboffers.domain.loginandregister.dto.RegisterUserDto;
import com.joboffers.domain.loginandregister.dto.RegistrationResultDto;
import com.joboffers.domain.loginandregister.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LoginAndRegisterFacade {
    private final LoginRepository loginRepository;

    public RegistrationResultDto register(RegisterUserDto registerUserDto) {
        User user = User.builder()
                .username(registerUserDto.username())
                .password(registerUserDto.password())
                .build();
        User savedUser = loginRepository.save(user);
        return new RegistrationResultDto(savedUser.id(), true, savedUser.username());
    }

    public UserDto findByUsername(String username) {
        User accountByUsername = loginRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("User not found"));
        return UserMapper.mapToDto(accountByUsername);
    }
}
