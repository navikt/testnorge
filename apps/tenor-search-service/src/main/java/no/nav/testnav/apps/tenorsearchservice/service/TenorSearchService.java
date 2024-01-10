package no.nav.testnav.apps.tenorsearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorClient;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorClient tenorClient;

    public Mono<TenorResponse> getTestdata(String testDataQuery) {

        return tenorClient.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "");
    }

    public Mono<TenorResponse> getTestdata(TenorRequest searchData) {

        var builder = new StringBuilder();
        builder.append(getFoedselsdato(searchData.getFoedselsdato()));
        builder.append(getDoedsdato(searchData.getDoedsdato()));
        builder.append(getIdentifikatorType(searchData.getIdentifikatorType()));
        builder.append(getKjoenn(searchData.getKjoenn()));
        builder.append(getPersonstatus(searchData.getPersonstatus()));
        builder.append(getSivilstand(searchData.getSivilstand()));


        builder.append(getRoller(searchData.getRoller()));

        return tenorClient.getTestdata(!builder.isEmpty() ? builder.substring(5) : "");
    }

    private String getRoller(List<TenorRequest.Roller> roller) {

        return (roller.isEmpty()) ? "" : "+and+tenorRelasjoner.brreg-er-fr:{dagligLeder:*}";
    }

    private String getSivilstand(TenorRequest.Sivilstand sivilstand) {

            if (isNull(sivilstand)) {
                return "";
            }

            return "+and+sivilstand:" + switch (sivilstand) {
                case EnkeEllerEnkemann -> "enkeEllerEnkemann";
                case Gift -> "gift";
                case GjenlevendePartner -> "gjenlevendePartner";
                case RegistrertPartner -> "registrertPartner";
                case Separert -> "separert";
                case SeparertPartner -> "separertPartner";
                case Skilt -> "skilt";
                case SkiltPartner -> "skiltPartner";
                case Ugift -> "ugift";
                case Uoppgitt -> "uoppgitt";
            };
    }

    private String getPersonstatus(TenorRequest.Personstatus personstatus) {

            if (isNull(personstatus)) {
                return "";
            }

            return "+and+personstatus:" + switch (personstatus) {
                case Bosatt -> "bosatt";
                case Doed -> "doed";
                case Forsvunnet -> "forsvunnet";
                case Foedselsregistrert -> "foedselsregistrert";
                case IkkeBosatt -> "ikkeBosatt";
                case Inaktiv -> "inaktiv";
                case Midlertidig -> "midlertidig";
                case Opphørt -> "opphørt";
                case Utflyttet -> "utflyttet";
            };
    }

    private String getKjoenn(TenorRequest.Kjoenn kjoenn) {

            if (isNull(kjoenn)) {
                return "";
            }

            return "+and+kjoenn:" + switch (kjoenn) {
                case Mann -> "mann";
                case Kvinne -> "kvinne";
            };
    }

    private String getFoedselsdato(TenorRequest.DatoIntervall foedselsdato) {

        if (isNull(foedselsdato)) {
            return "";
        }

        return "+and+foedselsdato:[" + foedselsdato.getFra() + "+to+" + foedselsdato.getTil() + "]";
    }
    private String getDoedsdato(TenorRequest.DatoIntervall doedsdato) {

        if (isNull(doedsdato)) {
            return "";
        }

        return "+and+doedsdato:[" + doedsdato.getFra() + "+to+" + doedsdato.getTil() + "]";
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
