package com.joboffers.domain.offer;

import com.joboffers.domain.offer.dto.OfferDto;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfferFacadeTest {
    private final OfferRepository offerRepository = new OfferRepositoryTestImpl();

    @Test
    public void it_should_find_offer_when_offer_was_saved() {
        //given
        String hash = "001";
        Offer savedOffer = Offer.builder()
                .hash(hash)
                .title("Junior Java Developer")
                .company("google")
                .salary("4000 - 7000 PLN")
                .url("google.com")
                .build();
        offerRepository.save(savedOffer);
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        OfferDto offerDtoById = offerFacade.findOfferById(hash);
        //then
        OfferDto expectedOfferDto = OfferMapper.mapToDto(savedOffer);
        assertThat(offerDtoById).isEqualTo(expectedOfferDto);
    }

    @Test
    public void it_should_throw_offer_not_found_exception_when_offer_not_found() {
        //given
        String hash = "001";
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        //then
        assertThrows(OfferNotFoundException.class, () -> offerFacade.findOfferById(hash), "Offer not found");
    }

    @Test
    public void it_should_throw_duplicate_key_exception_if_offer_with_this_url_already_exists() {
        //given
        String hash = "001";
        Offer alreadyExistingOffer = Offer.builder()
                .hash(hash)
                .title("Junior Java Developer")
                .company("google")
                .salary("4000 - 7000 PLN")
                .url("google.com")
                .build();
        offerRepository.save(alreadyExistingOffer);
        OfferDto offerToSave = OfferDto.builder()
                .hash("random")
                .title("random")
                .company("random")
                .salary("random")
                .url("google.com")
                .build();
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        //then
        assertThrows(DuplicateKeyException.class, () -> offerFacade.saveOffer(offerToSave), "Offer already exists");
    }

    @Test
    public void it_should_save_all_offers_if_none_of_them_exists() {
        //given
        List<OfferDto> offerDtoList = List.of(
                OfferDto.builder()
                        .hash("001")
                        .title("Junior Java Developer")
                        .company("google")
                        .salary("4000 - 7000 PLN")
                        .url("google.com")
                        .build(),
                OfferDto.builder()
                        .hash("002")
                        .title("Junior Java Developer")
                        .company("facebook")
                        .salary("4000 - 7000 PLN")
                        .url("facebook.com")
                        .build(),
                OfferDto.builder()
                        .hash("003")
                        .title("Junior Java Developer")
                        .company("microsoft")
                        .salary("4000 - 7000 PLN")
                        .url("microsoft.com")
                        .build());
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        List<OfferDto> savedOfferDtos = offerFacade.fetchAllOffersAndSaveAllIfNotExists(offerDtoList);
        //then
        assertThat(savedOfferDtos).isEqualTo(offerDtoList);
    }

    @Test
    public void it_should_save_only_offers_that_dont_already_exists() {
        //given
        Offer savedOffer = Offer.builder()
                .hash("001")
                .title("Junior Java Developer")
                .company("google")
                .salary("4000 - 7000 PLN")
                .url("google.com")
                .build();
        offerRepository.save(savedOffer);

        List<OfferDto> offerDtoList = List.of(
                OfferDto.builder()
                        .hash("001")
                        .title("Junior Java Developer")
                        .company("google")
                        .salary("4000 - 7000 PLN")
                        .url("google.com")
                        .build(),
                OfferDto.builder()
                        .hash("002")
                        .title("Junior Java Developer")
                        .company("facebook")
                        .salary("4000 - 7000 PLN")
                        .url("facebook.com")
                        .build(),
                OfferDto.builder()
                        .hash("003")
                        .title("Junior Java Developer")
                        .company("microsoft")
                        .salary("4000 - 7000 PLN")
                        .url("microsoft.com")
                        .build());
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        List<OfferDto> savedOfferDtos = offerFacade.fetchAllOffersAndSaveAllIfNotExists(offerDtoList);
        //then
        List<OfferDto> expectedOffers = List.of(OfferDto.builder()
                        .hash("002")
                        .title("Junior Java Developer")
                        .company("facebook")
                        .salary("4000 - 7000 PLN")
                        .url("facebook.com")
                        .build(),
                OfferDto.builder()
                        .hash("003")
                        .title("Junior Java Developer")
                        .company("microsoft")
                        .salary("4000 - 7000 PLN")
                        .url("microsoft.com")
                        .build());
        assertThat(savedOfferDtos).isEqualTo(expectedOffers);
    }

    @Test
    public void it_should_return_empty_list_if_there_are_no_offers() {
        //given
        OfferFacade offerFacade = new OfferFacade(offerRepository);
        //when
        List<OfferDto> allOffers = offerFacade.findAllOffers();
        //then
        assertThat(allOffers).isEqualTo(Collections.EMPTY_LIST);
    }
}