package com.joboffers.domain.loginandregister;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class AccountRepositoryTestImpl implements AccountRepository {
    private final Map<String, Account> accountList = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        accountList.put(account.username(), account);
        return account;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return Optional.ofNullable(accountList.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return accountList.containsKey(username);
    }
}
