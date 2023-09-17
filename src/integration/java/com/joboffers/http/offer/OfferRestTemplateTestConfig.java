package com.joboffers.http.offer;

import com.joboffers.domain.offer.OfferFetchable;
import com.joboffers.infrastructure.offer.http.OfferHttpClientConfig;
import org.springframework.web.client.RestTemplate;

public class OfferRestTemplateTestConfig extends OfferHttpClientConfig {
    public OfferFetchable remoteOfferClient(int port) {
        RestTemplate restTemplate = restTemplate(restTemplateResponseErrorHandler());
        return remoteOfferClient(restTemplate, "http://localhost", port);
    }
}
