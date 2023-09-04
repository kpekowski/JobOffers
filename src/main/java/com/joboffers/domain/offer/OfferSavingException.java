package com.joboffers.domain.offer;

import java.util.List;

public class OfferSavingException extends RuntimeException {
    public OfferSavingException(String message, List<Offer> jobOffers) {
        super(message + jobOffers);
    }
}
