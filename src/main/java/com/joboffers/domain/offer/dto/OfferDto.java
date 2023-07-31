package com.joboffers.domain.offer.dto;

import lombok.Builder;

@Builder
public record OfferDto(
        String hash,
        String title,
        String company,
        String salary,
        String url) {
}
