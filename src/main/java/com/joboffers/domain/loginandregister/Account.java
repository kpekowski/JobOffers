package com.joboffers.domain.loginandregister;

import lombok.Builder;

@Builder
record Account(
        String username,
        String password) {
}
