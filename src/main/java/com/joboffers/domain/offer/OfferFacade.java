package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferDto;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class OfferFacade {
    private final OfferRepository offerRepository;
    private final OfferService offerService;

    public OfferDto findOfferById(String id) {
        Offer offerById = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found"));
        return OfferMapper.mapToDto(offerById);
    }

    public List<OfferDto> findAllOffers() {
        List<Offer> allOffers = offerRepository.findAllOffers();
        return allOffers.stream()
                .map(OfferMapper::mapToDto)
                .toList();
    }

    public OfferDto saveOffer(OfferDto offerDto) {
        Offer offer = OfferMapper.mapFromDto(offerDto);
        if (offerRepository.existsByUrl(offer.url())) {
            throw new DuplicateKeyException("Offer with whis url already exists");
        }
        offerRepository.save(offer);
        return offerDto;
    }

    public List<OfferDto> fetchAllOffersAndSaveAllIfNotExists(List<OfferDto> offerDtos) {
        List<Offer> uniqueOffers = offerDtos.stream()
                .map(OfferMapper::mapFromDto)
                .filter(offer -> !offerRepository.existsByUrl(offer.url()))
                .toList();
        uniqueOffers.forEach(offerRepository::save);
        return uniqueOffers.stream()
                .map(OfferMapper::mapToDto)
                .toList();
    }
}
