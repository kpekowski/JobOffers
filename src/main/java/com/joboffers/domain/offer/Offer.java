package com.joboffers.domain.offer;

import lombok.Builder;

@Builder
record Offer(
        String hash,
        String title,
        String company,
        String salary,
        String url) {
}
