package no.nav.registre.bisys.consumer.rs;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.RelasjonsResponse;

@Slf4j
public class HodejegerenConsumer {

  private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_IDENTER =
      new ParameterizedTypeReference<List<String>>() {};
  private static final ParameterizedTypeReference<RelasjonsResponse> RESPONSE_TYPE_RELASJON =
      new ParameterizedTypeReference<RelasjonsResponse>() {};

  @Autowired private RestTemplate restTemplate;

  private UriTemplate hentFoedteIdenterUrl;
  private UriTemplate hentRelasjonerUrl;

  public HodejegerenConsumer(String hodejegerenServerUrl) {
    this.hentFoedteIdenterUrl =
        new UriTemplate(hodejegerenServerUrl + "/v1/foedte-identer/{avspillergruppeId}");
    this.hentRelasjonerUrl =
        new UriTemplate(
            hodejegerenServerUrl + "/v1/relasjoner-til-ident?ident={ident}&miljoe={miljoe}");
  }

  @Timed(
      value = "bisys.resource.latency",
      extraTags = {"operation", "hodejegeren"})
  public List<String> finnFoedteIdenter(Long avspillergruppeId) {
    RequestEntity getRequest =
        RequestEntity.get(hentFoedteIdenterUrl.expand(avspillergruppeId.toString())).build();
    List<String> levendeIdenter = new ArrayList<>();
    ResponseEntity<List<String>> response =
        restTemplate.exchange(getRequest, RESPONSE_TYPE_IDENTER);

    if (response.getBody() != null) {
      levendeIdenter.addAll(response.getBody());
    } else {
      log.error(
          "HodejegerenConsumer.finnFoedteIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
    }

    return levendeIdenter;
  }

  @Timed(
      value = "bisys.resource.latency",
      extraTags = {"operation", "hodejegeren"})
  public RelasjonsResponse hentRelasjonerTilIdent(String ident, String miljoe) {

    RequestEntity getRequest = RequestEntity.get(hentRelasjonerUrl.expand(ident, miljoe)).build();
    ResponseEntity<RelasjonsResponse> response =
        restTemplate.exchange(getRequest, RESPONSE_TYPE_RELASJON);

    RelasjonsResponse relasjonsResponse = new RelasjonsResponse();

    if (response.getBody() != null) {
      relasjonsResponse = response.getBody();
    } else {
      log.error(
          "HodejegerenConsumer.hentRelasjonerTilIdent: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
    }

    return relasjonsResponse;
  }
}
