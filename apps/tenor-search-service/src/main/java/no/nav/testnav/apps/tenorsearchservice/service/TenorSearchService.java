package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorClient;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorClient tenorClient;

    public Mono<TenorResponse> getTestdata(String testDataQuery) {

        return tenorClient.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "");
    }

    public Mono<TenorResponse> getTestdata(TenorRequest searchData) {

        var builder = new StringBuilder()
                .append(convertDatoer("foedselsdato", searchData.getFoedselsdato()))
                .append(convertDatoer("doedsdato", searchData.getDoedsdato()))
                .append(getIdentifikatorType(searchData.getIdentifikatorType()))
                .append(convertEnum("kjoenn", searchData.getKjoenn()))
                .append(convertEnum("personstatus", searchData.getPersonstatus()))
                .append(convertEnum("sivilstatus", searchData.getSivilstatus()))
                .append(getUtenlandskPersonidentifikasjon(searchData.getUtenlandskPersonIdentifikasjon()))
                .append(convertEnum("identitetsgrunnlagStatus", searchData.getIdentitetsgrunnlagStatus()))
                .append(convertEnum("adressebeskyttelse", searchData.getAdressebeskyttelse()))
                .append(convertBoolean("legitimasjonsdokument", searchData.getLegitimasjonsdokument()))
                .append(convertBoolean("falskIdentitet", searchData.getFalskIdentitet()))
                .append(convertBoolean("norskStatsborgerskap", searchData.getNorskStatsborgerskap()))
                .append(convertBoolean("flereStatsborgerskap", searchData.getFlereStatsborgerskap()));
        if (nonNull(searchData.getNavn())) {
            builder.append(convertBoolean("flereFornavn", searchData.getNavn().getFlereFornavn()))
                    .append(getNavnLengde(searchData.getNavn().getNavnLengde()))
                    .append(getHarMellomnavn(searchData.getNavn().getHarMellomnavn()))
                    .append(convertBoolean("navnSpesialtegn", searchData.getNavn().getNavnSpesialtegn()));
        }

        builder.append(getRoller(searchData.getRoller()));

        return tenorClient.getTestdata(!builder.isEmpty() ? builder.substring(5) : "");
    }

    private String getHarMellomnavn(Boolean harMellomnavn) {

        return isNotTrue(harMellomnavn) ? "" : "+and+harMellomnavn:*";
    }

    private String getNavnLengde(TenorRequest.NavnLengde navnLengde) {

        return isNull(navnLengde) ? "" : "+and+navnLengde:[%s+to+%s]"
                .formatted(isNull(navnLengde.getFraOgMed()) ? "*" : navnLengde.getFraOgMed(),
                        isNull(navnLengde.getTilOgMed()) ? "*" : navnLengde.getTilOgMed());
    }

    private String convertBoolean(String booleanNavn, Boolean booleanVerdi) {

        return isNull(booleanVerdi) ? "" : "+and+%s:%s".formatted(booleanNavn, booleanVerdi);
    }

    private String getUtenlandskPersonidentifikasjon(List<TenorRequest.UtenlandskPersonIdentifikasjon> utenlandskPersonIdentifikasjon) {

        return (utenlandskPersonIdentifikasjon.isEmpty()) ? "" : "+data+utenlandskPersonidentifikasjon:(%s)"
                .formatted(utenlandskPersonIdentifikasjon.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining("+and+")));
    }

    private String getRoller(List<TenorRequest.Roller> roller) {

        return (roller.isEmpty()) ? "" : "+and+tenorRelasjoner.brreg-er-fr:{dagligLeder:*}";
    }

    private String convertEnum(String enumNavn, Enum<?> enumVerdi) {

        return isNull(enumVerdi) ? "" : "+and+%s:%s%s".formatted(enumNavn,
                enumVerdi.name().substring(0, 1).toUpperCase(),
                enumVerdi.name().substring(1));
    }

    private String convertDatoer(String datoNavn, TenorRequest.DatoIntervall datoIntervall) {

        return isNull(datoIntervall) ? "" :
                "+and+%s:[%s+to+%s]".formatted(datoNavn, datoIntervall.getFra(), datoIntervall.getTil());
    }

    private String getIdentifikatorType(TenorRequest.IdentifikatorType identifikatorType) {

        if (isNull(identifikatorType)) {
            return "";
        }

        return "+and+identifikatorType:" + switch (identifikatorType) {
            case FNR -> "foedselsnummer";
            case DNR -> "dNummer";
            case FNR_TIDLIGERE_DNR -> "foedselsnummerOgTidligereDNummer";
        };
    }
}
