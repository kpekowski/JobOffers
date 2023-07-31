package com.joboffers.domain.offer;

import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    Optional<Offer> findById(String id);

    List<Offer> findAllOffers();

    Offer save(Offer offer);

    boolean existsByUrl(String url);
}
