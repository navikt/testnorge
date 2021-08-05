package no.nav.registre.inntekt.consumer.rs;

import static no.nav.registre.inntekt.consumer.rs.ConsumerUtils.buildInntektsliste;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InntektstubV2Consumer {

    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;

    private final UriTemplate leggTilInntektUrl;
    private final UriTemplate hentEksisterendeIdenterUrl;
    private final UriTemplate hentEksisterendeInntekterUrl;

    public InntektstubV2Consumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${inntektstub-u1.rest.api.url}") String inntektstubUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.leggTilInntektUrl = new UriTemplate(inntektstubUrl + "/v2/inntektsinformasjon?valider-inntektskombinasjoner=true&valider-kodeverk=true");
        this.hentEksisterendeIdenterUrl = new UriTemplate(inntektstubUrl + "/v2/personer");
        this.hentEksisterendeInntekterUrl = new UriTemplate(inntektstubUrl + "/v2/inntektsinformasjon?historikk=false&norske-identer={identer}");
    }

    public List<String> hentEksisterendeIdenter() {
        var getRequest = RequestEntity.get(hentEksisterendeIdenterUrl.expand()).build();
        return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }

    public List<Inntektsinformasjon> hentEksisterendeInntekterPaaIdenter(List<String> identer) {
        var partitions = Lists.partition(identer, PAGE_SIZE);
        List<Inntektsinformasjon> inntekter = new ArrayList<>();

        for (var page : partitions) {
            var identerAsString = String.join(",", page);
            var getRequest = RequestEntity.get(hentEksisterendeInntekterUrl.expand(identerAsString)).build();

            try {
                var responseBody = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<Inntektsinformasjon>>() {
                }).getBody();

                if (responseBody != null) {
                    inntekter.addAll(responseBody);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke hente inntekt fra inntektstub", e);
            }
        }

        return inntekter;
    }

    public List<Inntektsinformasjon> leggInntekterIInntektstub(Map<String, List<RsInntekt>> identerMedInntekter) {
        List<Inntektsinformasjon> inntektsinformasjonTilIdenter = new ArrayList<>();
        for (var identerMedInntekterEntry : identerMedInntekter.entrySet()) {
            for (var periodeVirksomhetInntekterEntry : buildInntektsliste(identerMedInntekterEntry.getValue()).entrySet()) {
                periodeVirksomhetInntekterEntry.getValue().forEach((virksomhet, value) -> {
                    var inntektsliste = value.getInntektsliste();
                    inntektsliste.forEach(ConsumerUtils::setBeskrivelseOgFordel);
                    inntektsinformasjonTilIdenter.add(
                            Inntektsinformasjon.builder()
                                    .norskIdent(identerMedInntekterEntry.getKey())
                                    .inntektsliste(inntektsliste)
                                    .aarMaaned(YearMonth.parse(periodeVirksomhetInntekterEntry.getKey()))
                                    .virksomhet(virksomhet)
                                    .opplysningspliktig(value.getOpplysningspliktig())
                                    .build());
                });
            }
        }

        var postRequest = RequestEntity.post(leggTilInntektUrl.expand()).body(inntektsinformasjonTilIdenter);
        List<Inntektsinformasjon> response = new ArrayList<>();
        try {
            List<Inntektsinformasjon> responseBody = restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<Inntektsinformasjon>>() {
            }).getBody();
            if (responseBody != null) {
                response = responseBody;
                for (var inntektsinformasjon : response) {
                    if (inntektsinformasjon.getFeilmelding() != null && !inntektsinformasjon.getFeilmelding().isEmpty()) {
                        for (var inntekt : inntektsinformasjon.getInntektsliste()) {
                            if (inntekt.getFeilmelding() != null && !inntekt.getFeilmelding().isEmpty()) {
                                log.info("Feil på innlegging av inntekt av type {} - feilmelding: {}", inntekt.getInntektstype(), inntektsinformasjon.getFeilmelding());
                            }
                        }
                    }
                }
            }
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge inntekt i inntektstub", e);
        }
        return response;
    }
}
