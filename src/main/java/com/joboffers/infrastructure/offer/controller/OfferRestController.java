package com.joboffers.infrastructure.offer.controller;

import com.joboffers.domain.offer.OfferFacade;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Log4j2
public class OfferRestController {
    private final OfferFacade offerFacade;

    @GetMapping("/offers")
    public ResponseEntity<List<OfferResponseDto>> offers() {
        List<OfferResponseDto> allOffers = offerFacade.findAllOffers();
        log.info(allOffers);
        return ResponseEntity.ok(allOffers);
    }
}
