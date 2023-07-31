package com.joboffers.domain.loginandregister;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
}
