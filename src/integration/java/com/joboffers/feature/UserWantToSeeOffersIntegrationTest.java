package com.joboffers.feature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.joboffers.BaseIntegrationTest;
import com.joboffers.SampleJobOfferResponse;
import com.joboffers.domain.loginandregister.dto.RegistrationResultDto;
import com.joboffers.domain.offer.dto.OfferResponseDto;
import com.joboffers.infrastructure.loginandregister.controller.dto.JwtResponseDto;
import com.joboffers.infrastructure.offer.scheduler.OfferScheduler;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
public class UserWantToSeeOffersIntegrationTest extends BaseIntegrationTest implements SampleJobOfferResponse {

    @Autowired
    OfferScheduler httpOffersScheduler;

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("offer.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("offer.http.client.config.port", () -> wireMockServer.getPort());
    }


    @Test
    public void user_want_to_see_offers_but_have_to_be_logged_in_and_external_server_should_have_some_offers() throws Exception {
        // step 1: there are no offers in external HTTP server
        //given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithZeroOffersJson())));


        // step 2: scheduler ran 1st time and made GET to external server and system added 0 offers to database
        // given && when
        List<OfferResponseDto> newOffers1 = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        //then
        assertThat(newOffers1).isEmpty();


        //step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        //given && when
        ResultActions failedLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        failedLoginRequest
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("""
                        {
                        "message": "Bad Credentials",
                        "status": "UNAUTHORIZED"
                        }
                        """.trim()));


        //step 4: user made GET /offers with no jwt token and system returned UNAUTHORIZED(401)
        //given && when
        ResultActions performGetWithoutToken = mockMvc.perform(get("/offers")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        performGetWithoutToken.andExpect(status().isForbidden());


        //step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status OK(200)
        //given && when
        ResultActions performRegister = mockMvc.perform(post("/register")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult registerActionResult = performRegister.andExpect(status().isCreated()).andReturn();
        String registerActionResultJson = registerActionResult.getResponse().getContentAsString();
        RegistrationResultDto registrationResultDto = objectMapper.readValue(registerActionResultJson, RegistrationResultDto.class);
        assertAll(
                () -> assertThat(registrationResultDto.username()).isEqualTo("someUser"),
                () -> assertThat(registrationResultDto.created()).isTrue(),
                () -> assertThat(registrationResultDto.id()).isNotNull()
        );


        //step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        //given && when
        ResultActions successLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        String json = successLoginRequest.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JwtResponseDto jwtResponse = objectMapper.readValue(json, JwtResponseDto.class);
        String token = jwtResponse.token();
        assertAll(
                () -> assertThat(jwtResponse.username()).isEqualTo("someUser"),
                () -> assertThat(token).matches(Pattern.compile("^([A-Za-z0-9-_=]+\\.)+([A-Za-z0-9-_=])+\\.?$"))
        );

        //step 7: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 0 offers
        //given
        String offersUrl = "/offers";
        //when
        ResultActions performGetResultWithZeroOffers = mockMvc.perform(get(offersUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        String jsonWithZeroOffers = performGetResultWithZeroOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithZeroOffers = objectMapper.readValue(jsonWithZeroOffers, new TypeReference<>() {
        });
        assertThat(parsedJsonWithZeroOffers).isEmpty();


        //step 8: there are 2 new offers in external HTTP server
        //given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithTwoOffersJson())));


        //step 9: scheduler ran 2nd time and made GET to external server and system added 2 new offers with ids: 1000 and 2000 to database
        // given && when
        List<OfferResponseDto> newOffers2 = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        //then
        assertThat(newOffers2).hasSize(2);


        //step 10: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 2 offers with ids: 1000 and 2000
        //given && when
        ResultActions performGetResultWithTwoOffers = mockMvc.perform(get(offersUrl)
                .header("Authorization", "Bearer " + token));
        //then
        String jsonWithTwoOffers = performGetResultWithTwoOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithTwoOffers = objectMapper.readValue(jsonWithTwoOffers, new TypeReference<>() {
        });

        OfferResponseDto expectedFirstOffer = newOffers2.get(0);
        OfferResponseDto expectedSecondOffer = newOffers2.get(1);

        assertThat(parsedJsonWithTwoOffers).containsExactlyInAnyOrder(
                new OfferResponseDto(expectedFirstOffer.id(), expectedFirstOffer.companyName(), expectedFirstOffer.position(), expectedFirstOffer.salary(), expectedFirstOffer.offerUrl()),
                new OfferResponseDto(expectedSecondOffer.id(), expectedSecondOffer.companyName(), expectedSecondOffer.position(), expectedSecondOffer.salary(), expectedSecondOffer.offerUrl())
        );

        //step 11: user made GET /offers/9999 and system returned NOT_FOUND(404) with message “Offer with id 9999 not found”
        //given
        String offersById9999Url = "/offers/9999";
        //when
        ResultActions performGetResultWithNotExistingId = mockMvc.perform(get(offersById9999Url)
                .header("Authorization", "Bearer " + token));
        //then
        performGetResultWithNotExistingId.andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                        "message": "Offer with id 9999 not found",
                        "status": "NOT_FOUND"
                        }
                        """.trim()
                ));


        //step 12: user made GET /offers/1000 and system returned OK(200) with offer
        //given
        String offersById1000Url = "/offers/" + expectedFirstOffer.id();
        //when
        ResultActions performGetOfferWithId1000 = mockMvc.perform(get(offersById1000Url)
                .header("Authorization", "Bearer " + token));
        //then
        String jsonOfferWithId1000 = performGetOfferWithId1000.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OfferResponseDto offerWithId1000Response = objectMapper.readValue(jsonOfferWithId1000, OfferResponseDto.class);

        assertThat(offerWithId1000Response).isEqualTo(expectedFirstOffer);


        //step 13: there are 2 new offers in external HTTP server
        //given && when && then
        wireMockServer.stubFor(WireMock.get("/offers")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyWithFourOffersJson())));


        //step 14: scheduler ran 3rd time and made GET to external server and system added 2 new offers with ids: 3000 and 4000 to database
        // given && when
        List<OfferResponseDto> newOffers3 = httpOffersScheduler.fetchAllOffersAndSaveAllIfNotExists();
        //then
        assertThat(newOffers3).hasSize(2);


        //step 15: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 4 offers with ids: 1000,2000, 3000 and 4000
        //given && when
        ResultActions performGetResultWithFourOffers = mockMvc.perform(get(offersUrl)
                .header("Authorization", "Bearer " + token));
        //then
        String jsonWithFourOffers = performGetResultWithFourOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithFourOffers = objectMapper.readValue(jsonWithFourOffers, new TypeReference<>() {
        });

        assertThat(parsedJsonWithFourOffers).hasSize(4);

        OfferResponseDto expectedThirdOffer = newOffers3.get(0);
        OfferResponseDto expectedFourthOffer = newOffers3.get(1);
        assertThat(parsedJsonWithFourOffers).contains(
                new OfferResponseDto(expectedFirstOffer.id(), expectedFirstOffer.companyName(), expectedFirstOffer.position(), expectedFirstOffer.salary(), expectedFirstOffer.offerUrl()),
                new OfferResponseDto(expectedSecondOffer.id(), expectedSecondOffer.companyName(), expectedSecondOffer.position(), expectedSecondOffer.salary(), expectedSecondOffer.offerUrl()),
                new OfferResponseDto(expectedThirdOffer.id(), expectedThirdOffer.companyName(), expectedThirdOffer.position(), expectedThirdOffer.salary(), expectedThirdOffer.offerUrl()),
                new OfferResponseDto(expectedFourthOffer.id(), expectedFourthOffer.companyName(), expectedFourthOffer.position(), expectedFourthOffer.salary(), expectedFourthOffer.offerUrl()
                ));

        //step 16: user made POST /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and offer as body and system returned CREATED(201) with saved offer
        //given && when
        ResultActions performPostOffersWithOneOffer = mockMvc.perform(post("/offers")
                .header("Authorization", "Bearer " + token)
                .content("""
                        {
                        "companyName": "someCompany",
                        "position": "somePosition",
                        "salary": "7 000 - 9 000 PLN",
                        "offerUrl": "https://newoffers.pl/offer/1234"
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String createdOfferJson = performPostOffersWithOneOffer.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OfferResponseDto parsedCreatedOfferJson = objectMapper.readValue(createdOfferJson, OfferResponseDto.class);
        String createdOfferId = parsedCreatedOfferJson.id();
        assertAll(
                () -> assertThat(parsedCreatedOfferJson.offerUrl()).isEqualTo("https://newoffers.pl/offer/1234"),
                () -> assertThat(parsedCreatedOfferJson.companyName()).isEqualTo("someCompany"),
                () -> assertThat(parsedCreatedOfferJson.salary()).isEqualTo("7 000 - 9 000 PLN"),
                () -> assertThat(parsedCreatedOfferJson.position()).isEqualTo("somePosition"),
                () -> assertThat(createdOfferId).isNotNull()
        );


        //step 17: user made GET /offers with header “Authorization: Bearer AAAA.BBBB.CCC” and system returned OK(200) with 3 offer
        // given & when
        ResultActions peformGetResultWithFiveOffers = mockMvc.perform(get(offersUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        String jsonWithFiveOffers = peformGetResultWithFiveOffers.andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OfferResponseDto> parsedJsonWithFiveOffers = objectMapper.readValue(jsonWithFiveOffers, new TypeReference<>() {
        });
        assertThat(parsedJsonWithFiveOffers).hasSize(5);
        assertThat(parsedJsonWithFiveOffers.stream().map(OfferResponseDto::id)).contains(createdOfferId);
    }
}