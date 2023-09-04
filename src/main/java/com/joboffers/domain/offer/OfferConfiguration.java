package com.joboffers.domain.offer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferConfiguration {
    @Bean
    OfferFacade offerFacade(OfferFetchable offerFetchable, OfferRepository repository) {
        OfferService offerService = new OfferService(offerFetchable, repository);
        return new OfferFacade(repository, offerService);
    }
}
