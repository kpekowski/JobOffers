package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferDto;

class OfferMapper {
    public static OfferDto mapToDto(Offer offer) {
        return OfferDto.builder()
                .hash(offer.hash())
                .salary(offer.salary())
                .title(offer.title())
                .url(offer.url())
                .company(offer.company())
                .build();
    }

    public static Offer mapFromDto(OfferDto offerDto) {
        return Offer.builder()
                .hash(offerDto.hash())
                .salary(offerDto.salary())
                .title(offerDto.title())
                .url(offerDto.url())
                .company(offerDto.company())
                .build();
    }
}
