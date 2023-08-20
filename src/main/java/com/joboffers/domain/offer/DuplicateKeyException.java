package com.joboffers.domain.offer;
import java.util.List;
import lombok.Getter;

@Getter
public class DuplicateKeyException extends RuntimeException {

    private final List<String> offerUrls;

    public DuplicateKeyException(String offerUrl) {
        super(String.format("Offer with offerUrl [%s] already exists", offerUrl));
        this.offerUrls = List.of(offerUrl);
    }

    public DuplicateKeyException(String message, List<Offer> offers) {
        super(String.format("error" + message + offers.toString()));
        this.offerUrls = offers.stream().map(Offer::url).toList();
    }
}