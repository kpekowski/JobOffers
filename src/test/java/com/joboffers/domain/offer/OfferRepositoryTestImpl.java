package com.joboffers.domain.offer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class OfferRepositoryTestImpl implements OfferRepository {
    private final Map<String, Offer> offerList = new ConcurrentHashMap<>();

    @Override
    public Optional<Offer> findById(String id) {
        return Optional.ofNullable(offerList.get(id));
    }

    @Override
    public List<Offer> findAllOffers() {
        return offerList.values()
                .stream()
                .toList();
    }

    @Override
    public Offer save(Offer offer) {
        offerList.put(offer.hash(), offer);
        return offer;
    }

    @Override
    public boolean existsByUrl(String url) {
        return offerList.values()
                .stream()
                .anyMatch(offer -> offer.url().equals(url));
    }
}
